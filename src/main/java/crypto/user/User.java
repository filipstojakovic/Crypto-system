package crypto.user;

import crypto.user.jsonhandler.UserJson;
import crypto.utils.CertificateUtil;
import crypto.utils.KeyPairUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class User
{
    //TODO: simetric key or smt
    private String username;
    private String commonName;
    private X509Certificate x509Certificate;
    private KeyPair keyPair;

    public User()
    {
    }

    public User(String username, String commonName, X509Certificate x509Certificate)
    {
        this.username = username;
        this.commonName = commonName;
        this.x509Certificate = x509Certificate;
    }

    public User(String username, String commonName, X509Certificate x509Certificate, KeyPair keyPair)
    {
        this.username = username;
        this.commonName = commonName;
        this.x509Certificate = x509Certificate;
        this.keyPair = keyPair;
    }

    public static User loadUser(@NotNull UserJson user) throws IOException
    {
        String username = user.getUsername();
        X509Certificate userCert = CertificateUtil.loadCertificate(user.getUsername());
        String commonName = CertificateUtil.getCommonNameFromCert(userCert);
        KeyPair userKeyPair = KeyPairUtil.loadUserKeyPair(user.getUsername());

        return new User(username,commonName,userCert,userKeyPair);
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
}
