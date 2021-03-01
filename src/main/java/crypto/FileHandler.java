package crypto;

import crypto.encrypdecrypt.SymmetricEncryption;
import crypto.exception.FileNotClosedException;
import crypto.user.User;
import crypto.exception.InvalidNumOfArguemntsException;
import crypto.utils.FileUtil;
import crypto.utils.PrintUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.Desktop;

import static crypto.MainApp.scanner;

public class FileHandler
{
    public static final String FILE_EXISTS = "file already exists";
    public static final String FILE_DOES_NOT_EXIST = "file does not exist";
    private User user;
    private String currentPath;
    private SymmetricEncryption symmetricEncryption;

    public FileHandler(User user)
    {
        this.user = user;
        symmetricEncryption = user.getSymmetricEncryption();
    }

    public FileHandler(User user, String currentPath)
    {
        this.user = user;
        this.currentPath = currentPath;
        symmetricEncryption = user.getSymmetricEncryption();
    }

    /**
     * @param input touch fileName
     * @return true if faile create, else false
     */
    public Path createFile(String input) throws IOException, InvalidNumOfArguemntsException
    {
//        if (!isArguemntValid(input))
//            throw new InvalidNumOfArguemntsException();

        String path = currentPath + File.separator + input.split(crypto.utils.Utils.REGEX_SPACES)[1];
        if (isFileExists(path))
        {
            throw new FileNotFoundException();
        }

        return Files.createFile(Paths.get(path));
    }

    /**
     * Open file in default application
     *
     * @param input open fileName
     * @throws IOException
     */
    public void openFile(String input) throws IOException, InvalidNumOfArguemntsException, FileNotClosedException
    {
//        if (!isArguemntValid(input))
//            return;

        String path = currentPath + File.separator + input.split(crypto.utils.Utils.REGEX_SPACES)[1];
        if (!isFileExists(path))
        {
            System.out.println(FILE_DOES_NOT_EXIST);
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        File file = Paths.get(path).toFile();
        if(!FileUtil.isFileClosed(file))
            throw new FileNotClosedException();
        else
            desktop.open(file);
    }

    /**
     * Delete file rm fileName
     *
     * @param input rm fileName
     * @return true if file delete, false if not deleted
     */
    public boolean removeFile(String input) throws IOException, InvalidNumOfArguemntsException
    {
//        if (!isArguemntValid(input))
//            return false;

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
    public void insetFileContent(Path path) throws Exception
    {
        String content = getContentFromUser();
        String key = CommandHandler.getKeyFromUser();

        var encrypted = symmetricEncryption.encrypt(key,content);

        Files.write(path, encrypted);
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
    public String getContentFromUser() throws IOException
    {
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        PrintUtil.printlnColorful("enter file content and \"exit\" for saving",PrintUtil.ANSI_YELLOW);
        while (!(line = scanner.readLine()).equals("exit"))
            stringBuilder.append(line).append("\n");

        if (!stringBuilder.isEmpty())
            stringBuilder.setLength(stringBuilder.length() - 1); // trim last space
        return stringBuilder.toString();
    }

    //check if file exists on the given path
    public static boolean isFileExists(String input)
    {
        return Files.exists(Paths.get(input));
    }

}
