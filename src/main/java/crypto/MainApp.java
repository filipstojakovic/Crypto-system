package crypto;

import crypto.utils.CertificateUtil;
import crypto.utils.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Scanner;

/**
 * @author Filip Stojakovic
 */
public class MainApp
{
    public static final Scanner scanner = new Scanner(System.in);
    public static final int LOGIN = 1;
    public static final int SIGNUP = 2;
    public static final int EXIT = 0;

    //fun start here
    public static void main(String[] args)
    {
        CertificateUtil.initRootCertificate();
        Security.addProvider(new BouncyCastleProvider());
        int command;
        do
        {
            Utils.clearScreen();
            command = loginOrSignUp();
            switch (command)
            {
                case LOGIN -> login();
                case SIGNUP -> signup();
            }

        } while (command != 0);

        System.out.println("Bye bye");
    }

    private static int loginOrSignUp()
    {
        int command;
        do
        {
            System.out.print("1)Login\n2)Sign up\n0)Exit\nCommand: ");
            command = scanner.nextInt();

        } while (command < 0 || command > 2);

        return command;
    }

    private static void signup()
    {
        SignUp signUp = new SignUp();
        signUp.handleSignUp();
    }

    private static void login()
    {
        Login login = new Login();
        login.handleLogin();
    }

}
