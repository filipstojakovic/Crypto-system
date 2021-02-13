import user.User;
import user.UserChecker;

import java.util.Scanner;

public class MainApp
{
    public static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args)
    {
        Utils.clearScreen();

        User user = getUserInfo();
        Command control = new Command(user);
        control.takeUserInputs();

        System.out.println("Bye");
    }

    private static User getUserInfo()
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
