package crypto;

import crypto.user.User;
import crypto.user.exceptions.InvalidNumOfArguemntsException;
import crypto.utils.FileUtil;
import crypto.utils.Constants;
import crypto.utils.PrintUtil;
import crypto.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Command
{
    public static final String FILE_NOT_FOUND = "file not found";
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

            } catch (InvalidNumOfArguemntsException ex)
            {
                PrintUtil.printlnErrorMsg(ex.getMessage());

            } catch (IOException ex)
            {
                PrintUtil.printlnErrorMsg(FILE_NOT_FOUND);
            }

        } while (!"exit".equals(input));
    }

    //true -valid input, else invalid input
    private void analyzeInput(String input) throws IOException, InvalidNumOfArguemntsException
    {

        var startWith = input.split("\\s")[0];
        if (startWith == null || startWith.isEmpty())
        {
            PrintUtil.printlnColorful("Invalid input!", PrintUtil.ANSI_RED);
            return;
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

            case "cd":
                cdCommand(input);
                break;

            case "mkdir":
                makeDirsCommand(input);
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
                    fileList.forEach(x ->
                    {
                        if (x.toFile().isFile())
                            System.out.println(x.getFileName().toString());
                        else
                            PrintUtil.printlnColorful(x.getFileName().toString(), PrintUtil.ANSI_YELLOW);
                    });
                break;

            case "clear":
                Utils.clearScreen();
                break;

            case "help":
                printAllCommands();
                break;

            case "exit":
                break;

        }

    }

    private void cdCommand(String input) throws IOException, InvalidNumOfArguemntsException
    {
        //TODO: make cd ~ and cd ..
        if (Utils.checkForTwoArguments(input))
        {
            var splted = input.split(Utils.REGEX_SPACES);
            String path = splted[1].replaceAll("\\|/", File.separator);
            String fullPath = pathBuilder.toString() + File.separator + path;
            if(Paths.get(fullPath).toFile().exists())
            {
                pathBuilder = new StringBuilder(fullPath);
            }else
                throw new IOException();
        }else
            throw new InvalidNumOfArguemntsException();
    }

    private void makeDirsCommand(String input) throws IOException
    {
        if (Utils.checkForTwoArguments(input))
        {
            var args = input.split(Utils.REGEX_SPACES);
            Path path = Paths.get(pathBuilder.toString(), args[1]);
            Files.createDirectories(path);
        } else
        {

        }
    }

    private void catCommand(String input) throws IOException, InvalidNumOfArguemntsException
    {
        var splited = input.split("\\s");
        if (splited.length == 2)
        {
            File file = FileUtil.getFileIfExists(pathBuilder.toString() + File.separator + splited[1]);
            if (file != null)
                Files.readAllLines(file.toPath()).forEach(System.out::println);
            else
                System.out.println("");
        } else
            throw new InvalidNumOfArguemntsException();
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

    private void printAllCommands()
    {
        System.out.println("open - opens the file in default program");
        System.out.println("touch - creates new file and adds content");
        System.out.println("mkdir - create directories");
        System.out.println("cat - print file content");
        System.out.println("rm - delete file/folder");
        System.out.println("ls - print current folder content");
        System.out.println("clear - clear screen");
        System.out.println("exit");
    }
}
