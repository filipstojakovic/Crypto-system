package crypto.jsonhandler;

import crypto.encrypdecrypt.AsymmetricEncryption;
import crypto.encrypdecrypt.HashUtil;
import crypto.encrypdecrypt.SymmetricEncryption;
import crypto.user.User;
import crypto.utils.Utils;
import org.bouncycastle.jcajce.provider.symmetric.AES;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class JsonSignatureHandler
{
    public static final String CONTENT = "content";
    public static final String SIGNATURE = "signature";

    public static final String SENDER = "sender";
    public static final String RECIEVER = "reciever";
    public static final String ENVELOPE = "envelope";
    public static final String SHARE_CONTENT = "sharecontent";
    public static final String SHARE_HASH_ALGO = "MD5";


    private final User user;
    private final SymmetricEncryption symmEncription;

    public JsonSignatureHandler(User user)
    {
        this.user = user;
        symmEncription = user.getSymmetricEncryption();
    }

    public JSONObject createSignatureWithEncryptedContent(byte[] content, String symmetricKey, String symmetricAlgo, String hashAlgo, Key key) throws Exception
    {
        var hashedContent = HashUtil.createHash(content, hashAlgo);
        var signature = AsymmetricEncryption.encryptWithKey(hashedContent.getBytes(StandardCharsets.UTF_8), key);

        var encryptedFileContent = symmEncription.encrypt(symmetricKey, content, symmetricAlgo);

        var contentBase64 = Base64.getEncoder().encodeToString(encryptedFileContent);
        var sinatureBase64 = Base64.getEncoder().encodeToString(signature);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CONTENT, contentBase64);
        jsonObject.put(SIGNATURE, sinatureBase64);

        return jsonObject;
    }

    public JSONObject createSignatureWithEncryptedContent(byte[] content, String symmetricKey, Key key) throws Exception
    {
        return createSignatureWithEncryptedContent(content, symmetricKey, symmEncription.getSymmetricAlgo(), user.getHashAlgo(), key);
    }

    public boolean isSignatureAltered(JSONObject jsonFile, String symmAlgo, String symmetricKey, String hashAlgo, Key aKey) throws Exception
    {
        var fileSignature = getSignatureFromJSON(jsonFile);
        var fileSignatureHash = AsymmetricEncryption.decryptWithKey(fileSignature, aKey);
        var signatureHash = new String(fileSignatureHash);

        var encryptedFileContent = getContentFromJSON(jsonFile);
        var decryptedFileContent = symmEncription.decrypt(symmetricKey, encryptedFileContent, symmAlgo);
        var contentHash = HashUtil.createHash(decryptedFileContent, hashAlgo);
        return !signatureHash.equals(contentHash);
    }

    public boolean isSignatureAltered(JSONObject jsonFile, String symmetricKey, Key aKey) throws Exception
    {
        return isSignatureAltered(jsonFile, symmEncription.getSymmetricAlgo(), symmetricKey, user.getHashAlgo(), aKey);
    }

    public JSONObject loadJsonFileContent(Path path) throws IOException, ParseException
    {
        if (!Files.exists(path))
            throw new FileNotFoundException();

        try (FileReader reader = new FileReader(path.toFile()))
        {
            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(reader);
            return (JSONObject) obj;
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public JSONObject createSharedSignature(byte[] fileContent, String shareUsername, X509Certificate shareUserCert) throws Exception
    {
        String symmetricSecretKey = SymmetricEncryption.generateRandomAESkey();
        var jsonSygnature = createSignatureWithEncryptedContent(fileContent, symmetricSecretKey, "AES", SHARE_HASH_ALGO, user.getKeyPair().getPrivate());

        var envelope = AsymmetricEncryption.encryptWithKey(symmetricSecretKey.getBytes(StandardCharsets.UTF_8), shareUserCert.getPublicKey());
        var envelopeBase64 = Base64.getEncoder().encodeToString(envelope);

        JSONObject jsonSharedFile = new JSONObject();
        jsonSharedFile.put(SENDER, user.getUsername());
        jsonSharedFile.put(RECIEVER, shareUsername);
        jsonSharedFile.put(ENVELOPE, envelopeBase64);
        jsonSharedFile.put(SHARE_CONTENT, jsonSygnature);
        return jsonSharedFile;
    }


    public byte[] getContentFromJSON(JSONObject fileJson) throws IllegalArgumentException
    {
        var base64Content = (String) fileJson.get(CONTENT);
        return Base64.getDecoder().decode(base64Content);
    }

    public byte[] getSignatureFromJSON(JSONObject fileJson) throws IllegalArgumentException
    {
        var base64Sinagure = (String) fileJson.get(SIGNATURE);
        return Base64.getDecoder().decode(base64Sinagure);
    }

    public byte[] getEnvelope(JSONObject jsonObject)
    {
        var envelopeBase64 = (String) jsonObject.get(ENVELOPE);
        return Base64.getDecoder().decode(envelopeBase64);
    }

    public String getSender(JSONObject jsonObject)
    {
        return (String) jsonObject.get(SENDER);
    }

    public String getReciever(JSONObject jsonObject)
    {
        return (String) jsonObject.get(RECIEVER);
    }

    public JSONObject getShareContentJson(JSONObject jsonObject)
    {
        return (JSONObject) jsonObject.get(SHARE_CONTENT);
    }

    public byte[] extractDecryptedSymmKey(JSONObject jsonObject, X509Certificate shareUserCert) throws Exception
    {
        var envelope = getEnvelope(jsonObject);
        return AsymmetricEncryption.decryptWithKey(envelope, user.getKeyPair().getPrivate());
    }
}
