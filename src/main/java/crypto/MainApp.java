package crypto;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import crypto.user.LoadUser;
import crypto.user.User;
import crypto.utils.Utils;
import org.json.simple.parser.ParseException;

/**
 * @author Filip Stojakovic
 */
public class MainApp
{
    public static final Scanner scanner = new Scanner(System.in);

    //fun start here
    public static void main(String[] args)
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
        System.out.println("Bye");
    }

    private static User getUserInfo() throws IOException, URISyntaxException, ParseException
    {
        crypto.user.UserChecker userChecker = new crypto.user.UserChecker();
        User user = null;
        do
        {
            System.out.print("Enter username: ");
            String username = scanner.next().trim();
            System.out.print("Enter password: ");
            String password = scanner.next().trim();
            user = userChecker.checkUser(username, password);

            if(user ==null)
            {
                System.out.println("Ooops! Wrong username or password!");
                scanner.nextLine();
            }

        } while (user == null);

        return user;
    }

}
