package crypto.user.jsonhandler;

import crypto.utils.Constants;
import crypto.utils.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

public class JsonHandler
{
    public static final String USERNAME = "username";
    public static final String HASH_ALG = "hashalg";
    public static final String SALT = "salt";
    public static final String PASSWORD = "password";
    public static final String CERT_PATH = "certpath";
    public static final String PRIV_KEY_PATH = "privatekeypath";

    //commonname NE treba ovdje, ide u certifikat
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

    //get all users in JSONArray format from resources/users.json file
    public static JSONArray getUsersJsonArray() throws IOException, URISyntaxException, ParseException
    {
        JSONArray usersArray = null;

        File userFile = Utils.getFileFromResource(Constants.USERS_JSON_PATH);
        if (userFile.length() == 0)
            return new JSONArray();

        FileReader reader = new FileReader(userFile);
        //Read JSON file
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(reader);
        usersArray = (JSONArray) obj;

        reader.close();

        return usersArray;
    }
}
