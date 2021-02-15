import user.User;
import user.UserChecker;
import utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
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
            User user = getUserInfo();
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
        UserChecker userChecker = new UserChecker();
        User user = null;
        do
        {
            System.out.print("Enter username: ");
            String username = scanner.next().trim();
            System.out.print("Enter password: ");
            String password = scanner.next().trim();
            user = userChecker.checkUser(username, password);

        } while (user == null);

        return user;
    }

}
