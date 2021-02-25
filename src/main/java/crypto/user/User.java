package crypto.user;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

public class User
{
    private String username;
    private String password;
    private String salt;
    private String hashalg;

    // TODO: split into UserJson, save username here and botom half
    private String commonName;
    private X509Certificate x509Certificate;
    private KeyPair keyPair;

    public User()
    {
    }

    public User(String username, String password, String salt, String hashalg)
    {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.hashalg = hashalg;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getSalt()
    {
        return salt;
    }

    public void setSalt(String salt)
    {
        this.salt = salt;
    }

    public String getHashalg()
    {
        return hashalg;
    }

    public void setHashalg(String hashalg)
    {
        this.hashalg = hashalg;
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
