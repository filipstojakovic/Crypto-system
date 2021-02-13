import user.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Command
{
    public static final String USER_DIR = "users" + File.separator;
    public static final String TERMINATOR = " > ";

    private User user;
    private StringBuilder pathBuilder;

    public Command(User user)
    {
        this.user = user;
        pathBuilder = newUserPathBuilder();
        isUserFolderExists();
    }

    public void takeUserInputs()
    {
        Utils.clearScreen();
        System.out.println("Welcome " + user.getUsername());
        MainApp.scanner.nextLine(); // flush scanner

        String input = null;
        do
        {
            try
            {
                System.out.print(pathBuilder.toString() + TERMINATOR);
                input = MainApp.scanner.nextLine();
                analyzeInput(input);

            } catch (IOException ex)
            {
                ex.printStackTrace();
            }

        } while (!"exit".equals(input));
    }

    //true -valid input, else invalid input
    private void analyzeInput(String input) throws IOException
    {

        var startWith = input.split("\\s")[0];
        if (startWith == null || startWith.isEmpty())
        {
            System.out.println("invalid input!");
        }

        boolean success = false;
        FileHandler fileHandler = new FileHandler(user, pathBuilder.toString());
        switch (startWith)
        {
            case "open":
                fileHandler.openFile(input);
                break;

            case "touch":
                Path path = fileHandler.createFile(input);
                if (path != null)
                    fileHandler.insetFileContent(path);
                break;

            case "rm":
                success = fileHandler.removeFile(input);
                if (success)
                    System.out.println("deleted");
                break;

            case "ls":
                Files.list(Paths.get(pathBuilder.toString())).forEach(x -> System.out.println(x.getFileName()));
                break;

            case "clear":
                Utils.clearScreen();
                break;

            case "exit":
                break;

        }

    }

    private StringBuilder newUserPathBuilder()
    {
        return new StringBuilder(USER_DIR + user.getUsername());
    }

    private void isUserFolderExists()
    {
        File file = new File(pathBuilder.toString());
        if (!file.exists())
            file.mkdirs();
    }
}
