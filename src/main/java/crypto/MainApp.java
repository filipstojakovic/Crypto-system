package crypto;

import crypto.utils.CertificateUtil;
import crypto.utils.Constants;
import crypto.utils.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
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
        try
        {
            Security.addProvider(new BouncyCastleProvider());

            initDirs();
            CertificateUtil.initRootCertificate();
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

        } catch (ParseException | URISyntaxException | NoSuchAlgorithmException | IOException e)
        {
            e.printStackTrace();
        }

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

    private static void signup() throws ParseException, IOException, URISyntaxException, NoSuchAlgorithmException
    {
        SignUp signUp = new SignUp();
        signUp.handleSignUp();
    }

    private static void login()
    {
        Login login = new Login();
        login.handleLogin();
    }

    public static void initDirs() throws IOException
    {
        Files.createDirectories(Paths.get(Constants.CERT_DIR));
        Files.createDirectories(Paths.get(Constants.PRIVATE_KEYS_DIR));
        Files.createDirectories(Paths.get(Constants.USER_DIR));
        Files.createDirectories(Paths.get(Constants.ROOT_CA_DIR));
    }
}
