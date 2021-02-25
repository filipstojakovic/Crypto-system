package crypto.user.jsonhandler;

//used for compairing input username and password with users in json file
public class UserJson
{
    private String username;
    private String hashalg;
    private String salt;
    private String password;

    public UserJson()
    {
    }

    public UserJson(String username, String hashalg, String salt, String password)
    {
        this.username = username;
        this.hashalg = hashalg;
        this.salt = salt;
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getHashalg()
    {
        return hashalg;
    }

    public void setHashalg(String hashalg)
    {
        this.hashalg = hashalg;
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
