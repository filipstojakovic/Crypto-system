package crypto;

import crypto.cyptoutil.CertificateUtil;
import crypto.utils.Constants;
import crypto.utils.PrintUtil;
import crypto.utils.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

/**
 * @author Filip Stojakovic
 */
public class MainApp
{
    public static final BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
    //    public static final Scanner scanner = new Scanner(System.in);
    public static final int LOGIN = 1;
    public static final int SIGNUP = 2;
    public static final int EXIT = 0;

    //fun start here
    public static void main(String[] args) throws IOException
    {
        Utils.clearScreen();
        Security.addProvider(new BouncyCastleProvider());
        initDirs();
        CertificateUtil.initRootCertificate();
        int command = -1;
        do
        {
            try
            {
                command = loginOrSignUp();
                switch (command)
                {
                    case LOGIN -> login();
                    case SIGNUP -> signup();
                }
            }catch(CertificateNotYetValidException | CertificateExpiredException ex)
            {
                PrintUtil.printlnErrorMsg("Invalid Certificate " + ex.getMessage());

            } catch (ParseException | URISyntaxException | NoSuchAlgorithmException | IOException e)
            {
                e.printStackTrace();
            }

        } while (command != 0);


        System.out.println("Bye bye");
    }

    private static int loginOrSignUp() throws IOException
    {
        int command = -1;
        String number = "";
        do
        {
            System.out.print("1)Login\n2)Sign up\n0)Exit\nCommand: ");

            try
            {
                number = scanner.readLine(); //TODO: uncomment this
//                number = "1";
                command = Integer.parseInt(number);

            } catch (NumberFormatException ex)
            {

            }

        } while (command < 0 || command > 2);

        return command;
    }

    private static void signup() throws ParseException, IOException, URISyntaxException, NoSuchAlgorithmException
    {
        SignUp signUp = new SignUp();
        signUp.handleSignUp();
    }

    private static void login() throws URISyntaxException, ParseException, IOException, CertificateNotYetValidException, CertificateExpiredException
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
