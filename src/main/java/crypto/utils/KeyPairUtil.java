package crypto.utils;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

    private static PublicKey loadUserPublicKey(X509Certificate usercert)
    {
        return usercert.getPublicKey();
    }


    private static PrivateKey loadUserPrivateKey(String username) throws FileNotFoundException, IOException
    {
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        try (PemReader pemReader = new PemReader(new FileReader
                ((Paths.get(Constants.PRIVATE_KEYS_DIR + username + KeyPairUtil.PRIVATE_KEY_EXTENSION).toFile()))))
        {
            PemObject pemObject = pemReader.readPemObject();
            var pemContent = pemObject.getContent();
            PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(pemContent);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGO);
            return keyFactory.generatePrivate(encodedKeySpec);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex)
        {
            ex.printStackTrace();
        }

        return null;
    }


    static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGO, "BC");
        keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }
}
