package user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.bouncycastle.util.encoders.Hex;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utils.Utils;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.simple.parser.ParseException;

public class UserChecker
{
    public static final String USER = "username";
    public static final String HASH_ALG = "hashalg";
    public static final String SALT = "salt";
    public static final String PASSWORD = "password";

    public UserChecker()
    {
    }

    /**
     * Check if user exists and it's certificate
     *
     * @return null if user does not exist, user if exists (with valid username and inputPassword)
     */
    public User checkUser(@NotNull String inputUsername, @NotNull String inputPassword) throws IOException, URISyntaxException, ParseException
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

        return isFound ? user : null;   //if found return user else return null
    }

    private boolean checkUserPassword(User user, String inputPassword) throws NoSuchAlgorithmException
    {
        MessageDigest messageDigest = MessageDigest.getInstance(user.getHashalg());
        messageDigest.reset();
        messageDigest.update(user.getSalt().getBytes());
        byte[] hashedInputPassword = messageDigest.digest(inputPassword.getBytes());
        String haxPassword = Utils.bytesToHex(hashedInputPassword);
        return haxPassword.equals(user.getPassword());
    }

    //get all users in JSONArray format from resources/users.json file
    public JSONArray getUsersJsonArray() throws IOException, URISyntaxException, ParseException
    {
        JSONArray usersArray = null;

        FileReader reader = new FileReader(Utils.getFileFromResource("users.json"));

        //Read JSON file
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(reader);
        usersArray = (JSONArray) obj;

        reader.close();

        return usersArray;
    }
}
