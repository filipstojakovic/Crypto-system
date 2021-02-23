package crypto;

import crypto.user.LoadUser;
import crypto.user.User;
import crypto.user.UserChecker;
import crypto.utils.Utils;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;

import static crypto.MainApp.scanner;

public class Login
{

    public void handleLogin()
    {
        try
        {
            Utils.clearScreen();
            User userJSON = getUserInfo();
            User user = new LoadUser().loadUser(userJSON);
            Command control = new Command(user);
            control.takeUserInputs();

        } catch (Exception e)
        {
            e.printStackTrace();

        }
    }

    private static User getUserInfo() throws IOException, URISyntaxException, ParseException
    {
        UserChecker userChecker = new UserChecker();
        User user = null;
        do
        {
            System.out.print("Enter username: ");
            String username = scanner.next().trim();
            System.out.print("Enter password: ");
            String password = scanner.next().trim();
            user = userChecker.checkUser(username, password);

            if (user == null)
            {
                System.out.println("Ooops! Wrong username or password!");
                scanner.nextLine();
            }

        } while (user == null);

        return user;
    }
}
