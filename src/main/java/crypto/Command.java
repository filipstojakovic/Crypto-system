package crypto;

import crypto.exception.FileAlteredException;
import crypto.exception.FileNotClosedException;
import crypto.user.User;
import crypto.exception.InvalidNumOfArguemntsException;
import crypto.utils.Constants;
import crypto.utils.PrintUtil;
import crypto.utils.Utils;
import org.json.simple.parser.ParseException;

import javax.crypto.BadPaddingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class Command
{
    public static final String FILE_NOT_FOUND = "file not found";
    private User user;
    private StringBuilder pathBuilder;
    private CommandHandler commandHandler;

    public Command(User user)
    {
        this.user = user;
        commandHandler = new CommandHandler(user);
        pathBuilder = resetUserPathBuilder();
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

            } catch (FileNotClosedException | InvalidNumOfArguemntsException | NoSuchFileException | ParseException ex)
            {
                PrintUtil.printlnErrorMsg("error with file");
            }catch(FileAlteredException | IllegalArgumentException ex)
            {
               PrintUtil.printlnErrorMsg(new FileAlteredException().getMessage());
            }
             catch (BadPaddingException ex)
            {
                PrintUtil.printlnErrorMsg("Invalid file key");

            } catch (DirectoryNotEmptyException ex)
            {
                PrintUtil.printlnErrorMsg("Directory not empty!");
            } catch (IOException ex)
            {
                PrintUtil.printlnErrorMsg(FILE_NOT_FOUND);
//                ex.printStackTrace();
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

        } while (!"exit".equals(input));
    }

    //true -valid input, else invalid input
    private void analyzeInput(String input) throws Exception
    {

        var startWith = input.split("\\s")[0];
        if (startWith == null || startWith.isEmpty())
        {
            PrintUtil.printlnColorful("Invalid input!", PrintUtil.ANSI_RED);
            return;
        }

        boolean success = false;
        switch (startWith)
        {
            case "open":
                commandHandler.open(input, pathBuilder.toString());
                break;

            case "touch":
                commandHandler.touch(input, pathBuilder.toString());
                break;

            case "cd":
                commandHandler.cd(input, pathBuilder);
                break;

            case "mkdir":
            case "mkdirs":
                commandHandler.mkdir(input, pathBuilder.toString());
                break;
            //TODO: make show shared folder content
            case "ls":
                commandHandler.ls(input, pathBuilder.toString());
                break;

            case "cat":
                commandHandler.cat(input, pathBuilder.toString());
                break;

            case "dog":
                commandHandler.dog(input, pathBuilder.toString());
                break;

            case "users":
                commandHandler.users(input);
                break;

            case "upload":
                commandHandler.upload(input, pathBuilder.toString());
                break;
            case "download":
                commandHandler.download(input, pathBuilder.toString());
                break;
            case "rm":
                commandHandler.rm(input, pathBuilder.toString());
                break;

            case "clear":
            case "cls":
                Utils.clearScreen();
                break;

            case "help":
                printAllCommands();
                break;

            case "exit":
                break;
        }

    }

    private StringBuilder resetUserPathBuilder()
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
        System.out.println("open [filePath] - opens the file in default program");
        System.out.println("touch [fileName]- creates new file and adds content");
        System.out.println("mkdir [directoryName] - create directories");
        System.out.println("cat [fileName] - print decrypted file content");
        System.out.println("dog [fileName] - print encrypted file content");
        System.out.println("rm [fileName]- delete file/folder (only empty folder)");
        System.out.println("upload [desktop fileName] - upload from desktop to users system current path");
        System.out.println("download [fileName] - download file from users system to desktop");
        System.out.println("ls - print current folder content");
        System.out.println("users - list all users");
        System.out.println("clear - clear screen");
        System.out.println("exit");
    }
}
