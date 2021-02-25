package crypto.user.jsonhandler;

import crypto.utils.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

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
