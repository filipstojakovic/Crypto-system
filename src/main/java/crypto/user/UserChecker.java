package crypto.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import crypto.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.simple.parser.ParseException;

public class UserChecker
{

    public UserChecker()
    {
    }

    /**
     * Check if cypto.user exists and it's certificate
     *
     * @return null if cypto.user does not exist, cypto.user if exists (with valid username and inputPassword)
     */
    public User checkUserExistence(@NotNull String inputUsername, @NotNull String inputPassword) throws IOException, URISyntaxException, ParseException
    {
        JSONArray userArray = getUsersJsonArray();
        User user = null;
        boolean isFound = false;
        ObjectMapper objectMapper = new ObjectMapper();

        for (int i = 0; i < userArray.size() && !isFound; i++)
        {
            try
            {
                JSONObject userObj = (JSONObject) userArray.get(i);
                user = objectMapper.readValue(userObj.toString(), User.class);
                if (inputUsername.equals(user.getUsername()))
                {
                    try
                    {
                        isFound = checkUserPassword(user, inputPassword);
                        //TODO: maybe break the loop if password does not match
                    } catch (NoSuchAlgorithmException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            } catch (UnrecognizedPropertyException ex)
            {
                ex.printStackTrace();
            }
        }

        return isFound ? user : null;   //if found return cypto.user else return null
    }

    private boolean checkUserPassword(User user, String inputPassword) throws NoSuchAlgorithmException
    {
        MessageDigest messageDigest = MessageDigest.getInstance(user.getHashalg());
        messageDigest.reset();
        messageDigest.update(user.getSalt().getBytes());
        byte[] hashedInputPassword = messageDigest.digest(inputPassword.getBytes());
        String haxPassword = crypto.utils.Utils.bytesToHex(hashedInputPassword);
        return haxPassword.equals(user.getPassword());
    }

    //get all users in JSONArray format from resources/users.json file
    public JSONArray getUsersJsonArray() throws IOException, URISyntaxException, ParseException
    {
        JSONArray usersArray = null;

        File userFile = Utils.getFileFromResource(crypto.utils.PathConsts.USERS_JSON);
        if(userFile.length()==0)
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
