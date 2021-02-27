package crypto;

import crypto.user.User;
import crypto.utils.FileUtil;
import crypto.utils.Constants;
import crypto.utils.PrintUtil;
import crypto.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Command
{

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
        System.out.println("Welcome " + user.getCommonName());
        String input = null;
        do
        {
            try
            {
                PrintUtil.printColorful(makeRelativePath(pathBuilder.toString()) + Constants.COMMAND_TERMINATOR);
                input = MainApp.scanner.readLine();
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
                fileHandler.openFile(input); //TODO: make tmp file that is decripted and opet that file, if saved overide original
                break;

            case "touch":
                Path path = fileHandler.createFile(input);
                if (path != null)
                    fileHandler.insetFileContent(path);
                break;

            case "cat":
                catCommand(input);
                break;

            case "rm":
                success = fileHandler.removeFile(input);
                if (success)
                    System.out.println("deleted");
                break;

            case "ls":
                var fileList = Files.list(Paths.get(pathBuilder.toString())).collect(Collectors.toList());
                if (fileList.isEmpty())
                    System.out.println("Empty folder");
                else
                    fileList.forEach(x -> System.out.println(x.getFileName()));
                break;

            case "clear":
                Utils.clearScreen();
                break;

            case "exit":
                break;

        }

    }

    private void catCommand(String input) throws IOException
    {
        var splited = input.split("\\s");
        if (splited.length == 2)
        {
            File file = FileUtil.getFileIfExists(pathBuilder.toString() + File.separator + splited[1]);
            if (file != null)
                Files.readAllLines(file.toPath()).forEach(System.out::println);
            else
                System.out.println("file not found");
        } else
            System.out.println("invalid num of arguemtns");
    }

    private StringBuilder newUserPathBuilder()
    {
        return new StringBuilder(Constants.USER_DIR + user.getUsername());
    }

    private void isUserFolderExists()
    {
        File file = new File(pathBuilder.toString());
        if (!file.exists())
            file.mkdirs();
    }

    private String makeRelativePath(String fullPath)
    {
        int startIndex = fullPath.indexOf(user.getUsername());
        String relativePath = "~" + File.separator;
        return relativePath + fullPath.substring(startIndex);
    }
}
