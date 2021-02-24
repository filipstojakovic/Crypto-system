package crypto;

import crypto.user.UserChecker;
import crypto.utils.PrintUtil;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static crypto.MainApp.scanner;

public class SignUp
{

    public void handleSignUp() throws ParseException, IOException, URISyntaxException
    {
        PrintUtil.printColorful("Enter common name: ", PrintUtil.ANSI_YELLOW);
        String commonName = scanner.nextLine().trim();

        String name = enterUserName("Enter username: ");
        String password = enterPassword();

        System.out.println(commonName + " " + name + " " + password);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private String enterUserName(String s) throws ParseException, IOException, URISyntaxException
    {
        JSONArray users = (new UserChecker()).getUsersJsonArray();
        List<String> usernameList = (List<String>) users.stream() // get all usernames from json
                .map(obj -> ((JSONObject) obj).get("username").toString())
                .collect(Collectors.toList());

        boolean isValid = true;
        String username = null;
        do
        {
            if(!isValid)
                PrintUtil.printlnColorful("Username already exist! Try again",PrintUtil.ANSI_RED);

            PrintUtil.printColorful(s, PrintUtil.ANSI_YELLOW);
            username = scanner.nextLine().trim();
            isValid = false;
        } while (username.isEmpty() || (usernameList != null && usernameList.contains(username)));

        return username;
    }

    private String enterPassword()
    {
        String password = null;
        String confirmPassword = null;
        boolean isValid = true;
        do
        {
            if (!isValid)
                PrintUtil.printlnColorful("Try again!", PrintUtil.ANSI_RED);

            PrintUtil.printColorful("Enter password: ", PrintUtil.ANSI_YELLOW);
            password = scanner.nextLine().trim();
            PrintUtil.printColorful("Confirm password: ", PrintUtil.ANSI_YELLOW);
            confirmPassword = scanner.nextLine().trim();
            isValid = false;

        } while (password.isEmpty() || !password.equals(confirmPassword));

        return password;
    }

    //use random hash algo and save to jason
}
