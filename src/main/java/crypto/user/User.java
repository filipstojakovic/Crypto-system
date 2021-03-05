package crypto.user;

import crypto.encrypdecrypt.SymmetricEncryption;
import crypto.jsonhandler.UserJson;
import crypto.encrypdecrypt.CertificateUtil;
import crypto.encrypdecrypt.KeyPairUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public class User
{
    private String username;
    private String commonName;
    private X509Certificate x509Certificate;
    private KeyPair keyPair;
    private SymmetricEncryption symmetricEncryption;
    private String hashAlgo;

    public User()
    {
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setHashAlgo(String hashAlgo)
    {
        this.hashAlgo = hashAlgo;
    }

    private User(String username, String hashAlgo, String commonName, X509Certificate x509Certificate, KeyPair keyPair, String symmetricAlgo)
    {
        this.username = username;
        this.hashAlgo = hashAlgo;
        this.commonName = commonName;
        this.x509Certificate = x509Certificate;
        this.keyPair = keyPair;
        this.symmetricEncryption = new SymmetricEncryption(symmetricAlgo);

    }

    public static User loadUser(@NotNull UserJson userJson) throws IOException, CertificateEncodingException
    {
        String username = userJson.getUsername();
        String hashAlgo = userJson.getHashAlgo();
        X509Certificate userCert = CertificateUtil.loadUserCertificate(userJson.getUsername());
        String commonName = CertificateUtil.getCommonNameFromCert(userCert);
        KeyPair userKeyPair = KeyPairUtil.loadUserKeyPair(userJson.getUsername());

        return new User(username,hashAlgo, commonName, userCert, userKeyPair, userJson.getSymmetricAlgo());
    }

    public String getUsername()
    {
        return username;
    }

    public String getCommonName()
    {
        return commonName;
    }

    public X509Certificate getX509Certificate()
    {
        return x509Certificate;
    }

    public KeyPair getKeyPair()
    {
        return keyPair;
    }

    public SymmetricEncryption getSymmetricEncryption()
    {
        return symmetricEncryption;
    }

    public String getHashAlgo()
    {
        return hashAlgo;
    }
}
