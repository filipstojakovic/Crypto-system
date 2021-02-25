package crypto.user;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

public class User
{
    //simetric key
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
