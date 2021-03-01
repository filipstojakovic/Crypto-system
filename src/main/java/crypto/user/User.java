package crypto.user;

import crypto.encrypdecrypt.SymmetricEncryption;
import crypto.user.jsonhandler.UserJson;
import crypto.encrypdecrypt.CertificateUtil;
import crypto.encrypdecrypt.KeyPairUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

public class User
{
    private String username;
    private String commonName;
    private X509Certificate x509Certificate;
    private KeyPair keyPair;
    private SymmetricEncryption symmetricEncryption;

    public User()
    {
    }

    public User(String username, String commonName, X509Certificate x509Certificate, KeyPair keyPair, String symmetricAlgo)
    {
        this.username = username;
        this.commonName = commonName;
        this.x509Certificate = x509Certificate;
        this.keyPair = keyPair;
        this.symmetricEncryption = new SymmetricEncryption(symmetricAlgo);

    }

    public static User loadUser(@NotNull UserJson userJson) throws IOException
    {
        String username = userJson.getUsername();
        X509Certificate userCert = CertificateUtil.loadUserCertificate(userJson.getUsername());
        String commonName = CertificateUtil.getCommonNameFromCert(userCert);
        KeyPair userKeyPair = KeyPairUtil.loadUserKeyPair(userJson.getUsername());

        return new User(username, commonName, userCert, userKeyPair, userJson.getSymmetricAlgo());
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getCommonName()
    {
        return commonName;
    }

    public void setCommonName(String commonName)
    {
        this.commonName = commonName;
    }

    public X509Certificate getX509Certificate()
    {
        return x509Certificate;
    }

    public void setX509Certificate(X509Certificate x509Certificate)
    {
        this.x509Certificate = x509Certificate;
    }

    public KeyPair getKeyPair()
    {
        return keyPair;
    }

    public void setKeyPair(KeyPair keyPair)
    {
        this.keyPair = keyPair;
    }

    public SymmetricEncryption getSymmetricEncryption()
    {
        return symmetricEncryption;
    }
}
