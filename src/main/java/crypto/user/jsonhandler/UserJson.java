package crypto.user.jsonhandler;

//used for compairing input username and password with users in json file
public class UserJson
{
    //atributi moraju imati isti naziv kao u json fajlu (case sensitive)
    private String username;
    private String hashAlgo;
    private String salt;
    private String password;
    private String symmetricAlgo;

    public UserJson()
    {
    }

    public UserJson(String username, String hashAlgo, String salt, String password, String symmetricAlgo)
    {
        this.username = username;
        this.hashAlgo = hashAlgo;
        this.salt = salt;
        this.password = password;
        this.symmetricAlgo = symmetricAlgo;
    }

    public String getSymmetricAlgo()
    {
        return symmetricAlgo;
    }

    public void setSymmetricAlgo(String symmetricAlgo)
    {
        this.symmetricAlgo = symmetricAlgo;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getHashAlgo()
    {
        return hashAlgo;
    }

    public void setHashAlgo(String hashAlgo)
    {
        this.hashAlgo = hashAlgo;
    }

    public String getSalt()
    {
        return salt;
    }

    public void setSalt(String salt)
    {
        this.salt = salt;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
