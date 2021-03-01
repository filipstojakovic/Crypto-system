package crypto.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import crypto.user.jsonhandler.JsonHandler;
import crypto.user.jsonhandler.UserJson;
import crypto.encrypdecrypt.HashUtil;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
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
    public UserJson checkUserExistence(@NotNull String inputUsername, @NotNull String inputPassword) throws IOException, ParseException
    {
        JSONArray userArray = JsonHandler.getUsersJsonArray();
        UserJson userJson = null;
        boolean isFound = false;
        ObjectMapper objectMapper = new ObjectMapper();

        for (int i = 0; i < userArray.size(); i++)
        {
            try
            {
                JSONObject userObj = (JSONObject) userArray.get(i);
                userJson = objectMapper.readValue(userObj.toString(), UserJson.class);
                if (inputUsername.equals(userJson.getUsername()))
                {
                    isFound = checkUserPassword(userJson, inputPassword);
                    break;
                }
            } catch (UnrecognizedPropertyException | NoSuchAlgorithmException ex)
            {
                ex.printStackTrace();
            }
        }

        return isFound ? userJson : null;   //if found return userJson else return null
    }

    private boolean checkUserPassword(UserJson userJson, String inputPassword) throws NoSuchAlgorithmException
    {
        String haxPassword = HashUtil.hashedPassword(inputPassword, userJson.getSalt(), userJson.getHashAlgo());
        return haxPassword.equals(userJson.getPassword());
    }

}
