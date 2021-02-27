package crypto;

import crypto.user.User;
import crypto.user.UserChecker;
import crypto.user.jsonhandler.UserJson;
import crypto.utils.PrintUtil;
import crypto.utils.Utils;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;

import static crypto.MainApp.scanner;

public class Login
{
    public void handleLogin() throws ParseException, IOException, URISyntaxException
    {

        Utils.clearScreen();
        UserJson userJSON = getUserInfo();

        if (userJSON != null)
        {
            User user = User.loadUser(userJSON);
            Command control = new Command(user);
            control.takeUserInputs();
        }
    }

    private static UserJson getUserInfo() throws IOException, URISyntaxException, ParseException
    {
        UserChecker userChecker = new UserChecker();
        UserJson user = null;
        do
        {
            System.out.print("Enter username: ");
            String username = scanner.readLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.readLine().trim();
            user = userChecker.checkUserExistence(username, password);

            if (user == null)
            {
                PrintUtil.printlnErrorMsg("Ooops! Wrong username or password!");
            }

        } while (user == null);

        return user;
    }
}
