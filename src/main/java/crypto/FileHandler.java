package crypto;

import crypto.user.User;
import crypto.user.exceptions.InvalidNumOfArguemntsException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.Desktop;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public class FileHandler
{
    public static final String FILE_EXISTS = "file already exists";
    public static final String FILE_DOES_NOT_EXIST = "file does not exist";
    private User user;
    private String currentPath;

    public FileHandler(User user, String currentPath)
    {

        this.user = user;
        this.currentPath = currentPath;
    }

    /**
     * @param input touch fileName
     * @return true if faile create, else false
     */
    public Path createFile(String input) throws IOException, InvalidNumOfArguemntsException
    {
        if (!isArguemntValid(input))
            return null;

        String path = currentPath + File.separator + input.split(crypto.utils.Utils.REGEX_SPACES)[1];
        if (isFileExists(path))
        {
            System.out.println(FILE_EXISTS);
            return null;
        }

        return Files.createFile(Paths.get(path));
    }

    /**
     * Open file in default application
     *
     * @param input open fileName
     * @throws IOException
     */
    public void openFile(String input) throws IOException, InvalidNumOfArguemntsException
    {
        if (!isArguemntValid(input))
            return;

        String path = currentPath + File.separator + input.split(crypto.utils.Utils.REGEX_SPACES)[1];
        if (!isFileExists(path))
        {
            System.out.println(FILE_DOES_NOT_EXIST);
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        desktop.open(Paths.get(path).toFile());
    }

    /**
     * Delete file rm fileName
     *
     * @param input rm fileName
     * @return true if file delete, false if not deleted
     */
    public boolean removeFile(String input) throws IOException, InvalidNumOfArguemntsException
    {
        if (!isArguemntValid(input))
            return false;

        String path = currentPath + File.separator + input.split(crypto.utils.Utils.REGEX_SPACES)[1];
        if (!isFileExists(path))
        {
            System.out.println(FILE_DOES_NOT_EXIST);
            return false;
        }
        return Files.deleteIfExists(Paths.get(path));
    }

    /**
     * Get cypto.user input and save content to the file
     *
     * @param path
     */
    public void insetFileContent(Path path) throws IOException
    {
        String content = getContentFromUser();
        Files.write(path, content.getBytes());
    }

    public byte[] readAllBytes(Path path) throws IOException
    {
        return Files.readAllBytes(path);
    }

    public void writeBytes(Path path, byte[] data) throws IOException
    {
        Files.write(path, data);
    }

    /**
     * get content from cypto.user until he enters "exit"
     *
     * @return cypto.user content
     */
    private String getContentFromUser() throws IOException
    {
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        System.out.println("enter file content and \"exit\" for saving");
        while (!(line = MainApp.scanner.readLine()).equals("exit"))
            stringBuilder.append(line).append("\n");

        if (!stringBuilder.isEmpty())
            stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    //check if file exists on the given path
    public static boolean isFileExists(String input)
    {
        return Files.exists(Paths.get(input));
    }

    //check if arguemnt when splited by space has lenght of 2
    private boolean isArguemntValid(String argument) throws InvalidNumOfArguemntsException
    {
        var inputSplit = argument.split(crypto.utils.Utils.REGEX_SPACES);
        if (inputSplit.length != 2)
        {
            throw new InvalidNumOfArguemntsException();
        }
        return true;
    }


}
