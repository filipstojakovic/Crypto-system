package user;

public class User
{
    private String username;
    private String password;
    private String salt;
    private String hashalg;

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

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

}
