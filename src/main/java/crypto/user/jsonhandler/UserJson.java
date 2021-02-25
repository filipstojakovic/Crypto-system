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
    private String certPath;
    private String privateKeyPath;

    public UserJson()
    {
    }

    public UserJson(String username, String hashalg, String salt, String password, String certPath, String privateKeyPath)
    {
        this.username = username;
        this.hashalg = hashalg;
        this.salt = salt;
        this.password = password;
        this.certPath = certPath;
        this.privateKeyPath = privateKeyPath;
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

    public String getCertPath()
    {
        return certPath;
    }

    public void setCertPath(String certPath)
    {
        this.certPath = certPath;
    }

    public String getPrivateKeyPath()
    {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath)
    {
        this.privateKeyPath = privateKeyPath;
    }
}
