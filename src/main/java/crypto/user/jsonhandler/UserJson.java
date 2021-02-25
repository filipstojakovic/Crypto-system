package crypto.user.jsonhandler;

import org.json.simple.JSONObject;

public class UserJson
{
    public static final String USERNAME = "username";
    public static final String HASH_ALG = "hashalg";
    public static final String SALT = "salt";
    public static final String PASSWORD = "password";
    public static final String CERT_PATH = "certpath";
    public static final String PRIV_KEY_PATH = "privatekeypath";

    private String username;
    private String hashalg;
    private String salt;
    private String password;
    private String certPath;
    private String privateKeyPath;



    //common name ide u certifikat, ne treba ovdje
    public static JSONObject createUserJson(String username, String hashalg, String salt,
                                            String hashedPassword, String certPath, String keyPath)
    {
        JSONObject userJson = new JSONObject();
        userJson.put(USERNAME, username);
        userJson.put(HASH_ALG, hashalg);
        userJson.put(SALT, salt);
        userJson.put(PASSWORD, hashedPassword);
        userJson.put(CERT_PATH, certPath);
        userJson.put(PRIV_KEY_PATH, keyPath);

        return userJson;
    }
}
