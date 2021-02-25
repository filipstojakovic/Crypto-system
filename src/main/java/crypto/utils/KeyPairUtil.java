package crypto.utils;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class KeyPairUtil
{
    public static final String RSA_ALGO = "RSA";
    public static final String PRIVATE_KEY_EXTENSION = "Private.key";
    public static final int KEY_SIZE = 2048;

    public static KeyPair loadUserKeyPair(String username) throws IOException
    {
        PublicKey publicKey = loadUserPublicKey(username);
        PrivateKey privateKey = loadUserPrivateKey(username);

        return (privateKey != null && publicKey != null) ? new KeyPair(publicKey, privateKey) : null;
    }

    private static PublicKey loadUserPublicKey(String username) throws FileNotFoundException
    {
        X509Certificate usercert = CertificateUtil.loadCertificate(username);
        return usercert.getPublicKey();
    }

    static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGO, "BC");
        keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    private static PublicKey loadUserPublicKey(X509Certificate usercert)
    {
        return usercert.getPublicKey();
    }

    public static String getPrivateKeyPath(String username)
    {
        return Constants.PRIVATE_KEYS_DIR + username + KeyPairUtil.PRIVATE_KEY_EXTENSION;
    }

    private static PrivateKey loadUserPrivateKey(String username) throws IOException
    {
        Path path = Paths.get(getPrivateKeyPath(username));
        return loadPrivateKey(path);
    }

    public static PrivateKey loadPrivateKey(Path path) throws IOException // maybe add algorithm for KeyFactory.getInstance
    {
        try (PemReader pemReader = new PemReader(new FileReader(path.toFile())))
        {
            PemObject pemObject = pemReader.readPemObject();
            var pemContent = pemObject.getContent();
            PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(pemContent);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGO);
            return keyFactory.generatePrivate(encodedKeySpec);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    static void savePrivateKeyToFile(PrivateKey privateKey, String path)
    {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path))
        {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                    privateKey.getEncoded());
            fileOutputStream.write(pkcs8EncodedKeySpec.getEncoded());
            fileOutputStream.flush();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
