package crypto.user.jsonhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import crypto.utils.Constants;
import crypto.utils.FileUtil;
import crypto.utils.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

public class JsonHandler
{
    public static final String USERNAME = "username";
    public static final String HASH_ALG = "hashAlgo";
    public static final String SALT = "salt";
    public static final String PASSWORD = "password";
    public static final String SYMMETRIC_ALGO = "symmetricAlgo";

    //commonName NE treba ovdje, ide u certifikat
    public static JSONObject createUserJson(String username, String hashalg, String salt, String hashedPassword, String symmetricAlgo)
    {
        JSONObject userJson = new JSONObject();
        userJson.put(USERNAME, username);
        userJson.put(HASH_ALG, hashalg);
        userJson.put(SALT, salt);
        userJson.put(PASSWORD, hashedPassword);
        userJson.put(SYMMETRIC_ALGO,symmetricAlgo);

        return userJson;
    }

    public static void saveUserJsonToFile(JSONObject userJsonObj) throws ParseException, IOException
    {
        JSONArray jsonArray = getUsersJsonArray();
        jsonArray.add(userJsonObj);

        ObjectMapper mapper = new ObjectMapper();
        String prittfyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonArray);

        try(FileWriter fileWriter = new FileWriter(Constants.USERS_JSON_PATH))
        {
            fileWriter.write(prittfyJson);
        }catch (IOException ex)
        {
            ex.printStackTrace();
        }

    }

    //get all users in JSONArray format from resources/users.json file
    public static JSONArray getUsersJsonArray() throws IOException, ParseException
    {
        JSONArray usersArray = null;

        File userFile = FileUtil.createFileIfNeeded(Constants.USERS_JSON_PATH);
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
