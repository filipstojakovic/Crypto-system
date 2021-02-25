package crypto;

import crypto.user.jsonhandler.JsonHandler;
import crypto.utils.CertificateUtil;
import crypto.utils.HashUtil;
import crypto.utils.PrintUtil;
import crypto.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static crypto.MainApp.scanner;

public class SignUp
{
    public static final int MIN_SALT_SIZE = 5;
    public static final int MAX_SALT_SIZE = 15;

    public void handleSignUp() throws ParseException, IOException, URISyntaxException, NoSuchAlgorithmException
    {
        String commonName = enterCommonName();
        String userName = enterUserName();
        String password = enterPassword();

        String hashAlg = randomHashAlg();
        String randomSalt = randomSalt();
        String hashedPassword = HashUtil.hashedPassword(password,randomSalt, hashAlg);

        CertificateUtil.generateSignedUserCert(commonName,userName); // creates Cert and private key

        JSONObject userJsonObj = JsonHandler.createUserJson(userName,hashAlg,randomSalt,hashedPassword);
        JsonHandler.saveUserJsonToFile(userJsonObj);
    }

    private String randomSalt()
    {
        Random random = new SecureRandom();
        int saltLength = MIN_SALT_SIZE + random.nextInt(MAX_SALT_SIZE - MIN_SALT_SIZE);
        return Utils.randomString(saltLength);
    }

    private String randomHashAlg()
    {
        List<String> hashAlgo = HashUtil.getAllHashAlgo();
        int randomNum = (new SecureRandom()).nextInt(hashAlgo.size());
        return hashAlgo.get(randomNum);
    }

    @NotNull
    private String enterCommonName()
    {
        PrintUtil.printColorful("Enter common name: ", PrintUtil.ANSI_YELLOW);
        scanner.nextLine();
        return scanner.nextLine().trim();
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private String enterUserName() throws ParseException, IOException, URISyntaxException
    {
        JSONArray users = JsonHandler.getUsersJsonArray();
        List<String> usernameList = (List<String>) users.stream() // get all usernames from json
                .map(obj -> ((JSONObject) obj).get("username").toString())
                .collect(Collectors.toList());

        boolean isValid = true;
        String username = null;
        do
        {
            if (!isValid)
                PrintUtil.printlnColorful("Username already exist! Try again", PrintUtil.ANSI_RED);

            PrintUtil.printColorful("Enter username: ", PrintUtil.ANSI_YELLOW);
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

}
