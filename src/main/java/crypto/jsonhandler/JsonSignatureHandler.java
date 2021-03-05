package crypto.jsonhandler;

import crypto.encrypdecrypt.AsymmetricEncryption;
import crypto.encrypdecrypt.HashUtil;
import crypto.encrypdecrypt.SymmetricEncryption;
import crypto.user.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.util.Base64;

public class JsonSignatureHandler
{
    public static final String CONTENT = "content";
    public static final String SIGNATURE = "signature";

    private final User user;
    private final SymmetricEncryption symmetricEncryption;

    public JsonSignatureHandler(User user)
    {
        this.user = user;
        symmetricEncryption = user.getSymmetricEncryption();
    }

    public JSONObject createSignatureWithEncryptedContent(byte[] content, String symmetricKey, Key key) throws Exception
    {
        var hashedContent = HashUtil.createHash(content, user.getHashAlgo());
        var signature = AsymmetricEncryption.encryptWithKey(hashedContent.getBytes(StandardCharsets.UTF_8), key);

        var encryptedFileContent = symmetricEncryption.encrypt(symmetricKey, content);

        var contentBase64 = Base64.getEncoder().encodeToString(encryptedFileContent);
        var sinatureBase64 = Base64.getEncoder().encodeToString(signature);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CONTENT, contentBase64);
        jsonObject.put(SIGNATURE, sinatureBase64);

        return jsonObject;
    }

    public boolean verifySignature(JSONObject jsonFile,String symmetricKey, Key aKey) throws Exception
    {
        var fileSignature = getSignatureFromJSON(jsonFile);
        var fileSignatureHash = AsymmetricEncryption.decryptWithKey(fileSignature, aKey);
        var signatureHash = new String(fileSignatureHash);

        var encryptedFileContent = getContentFromJSON(jsonFile);
        var decryptedFileContent = symmetricEncryption.decrypt(symmetricKey, encryptedFileContent);
        var contentHash = HashUtil.createHash(decryptedFileContent, user.getHashAlgo());
        return !signatureHash.equals(contentHash);

    }

    public JSONObject loadFileContent(Path path) throws IOException, ParseException
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


}
