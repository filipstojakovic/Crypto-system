package crypto;

import crypto.encrypdecrypt.SymmetricEncryption;
import crypto.exception.InvalidNumOfArguemntsException;
import crypto.user.User;
import crypto.utils.Constants;
import crypto.utils.FileUtil;
import crypto.utils.PrintUtil;
import crypto.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static crypto.MainApp.scanner;

public class CommandHandler
{
    private final User user;
    private final SymmetricEncryption symmetricEncryption;
    private final FileHandler fileHandler;

    public CommandHandler(User user)
    {
        this.user = user;
        symmetricEncryption = user.getSymmetricEncryption();
        fileHandler = new FileHandler(user);
    }

    public void mkdir(String input, String currentPath) throws InvalidNumOfArguemntsException, IOException
    {
        var splitInput = Utils.splitInputArguments(input);
        Path path = Paths.get(currentPath, Utils.replaceFileSeparator(splitInput[1]));
        Files.createDirectories(path);
    }

    public void cd(String input, StringBuilder currentPath) throws InvalidNumOfArguemntsException, FileNotFoundException
    {
        var splitInput = Utils.splitInputArguments(input);

        switch (splitInput[1])
        {
            case ".." -> {
                Path path = Paths.get(currentPath.toString());
                Path perentPath = path.getParent();
                Path perentName = perentPath.getFileName();
                if (!Constants.USER_DIR_NAME.equals(perentName.toString()))
                {
                    currentPath.setLength(0);
                    currentPath.append(perentPath.toString());
                }
            }
            case "~" -> {
                currentPath.setLength(0);
                currentPath.append(Constants.USER_DIR).append(user.getUsername());
            }
            default -> {
                String pathString = currentPath.toString() + File.separator + Utils.replaceFileSeparator(splitInput[1]);
                Path path = Paths.get(pathString);
                if (!Files.exists(path) || !Files.isDirectory(path))
                    throw new FileNotFoundException();

                currentPath.delete(0, currentPath.length());
                currentPath.append(pathString);
            }
        }
    }

    public void touch(String input, String currentPath) throws Exception
    {
        var splitInput = Utils.splitInputArguments(input);
        String pathString = currentPath + File.separator + Utils.replaceFileSeparator(splitInput[1]);
        Files.createFile(Paths.get(pathString));

        String content = fileHandler.getContentFromUser();
        String key = getKeyFromUser();
        var encryptedData = symmetricEncryption.encrypt(key, content);
        Files.write(Paths.get(pathString), encryptedData);
    }

    public void open(String input, String currentPath) throws Exception
    {
        var splitInput = Utils.splitInputArguments(input);
        String originalPathString = currentPath + File.separator + Utils.replaceFileSeparator(splitInput[1]);
        Path orginalPath = Paths.get(originalPathString);
        if (Files.isDirectory(orginalPath) || !Files.exists(orginalPath))
            throw new FileNotFoundException();

        Path copyPath = copyFilePath(originalPathString, orginalPath);
        FileUtils.copyFile(orginalPath.toFile(), copyPath.toFile());

        String key = getKeyFromUser();
        String content = symmetricEncryption.decrtpyToString(key, copyPath.toFile());

        Files.writeString(copyPath, content);
        Desktop.getDesktop().open(copyPath.toFile());   //open copy file

        PrintUtil.printColorful("Save file content? (Y\\N): ", PrintUtil.ANSI_GREEN);
        String userInput = scanner.readLine().toLowerCase().trim();
        if ("y".equals(userInput) || "yes".equals(userInput))
        {
            var data = Files.readAllBytes(copyPath);
            var encrypedData = symmetricEncryption.encrypt(key, data);
            Files.write(orginalPath, encrypedData);
        }
        Files.delete(copyPath);
    }

    static String getKeyFromUser() throws IOException
    {
        PrintUtil.printColorful("Please enter file key: ", PrintUtil.ANSI_YELLOW);
        return scanner.readLine().trim();
    }

    @NotNull
    private Path copyFilePath(String pathString, Path path) throws IOException
    {
        String originalFileName = path.getFileName().toString();
        String copyFileName = "copy_" + originalFileName;

        String copyFilePath = pathString.replace(originalFileName, copyFileName);
        return Paths.get(copyFilePath);
    }

    public void ls(String input, String currentPath) throws InvalidNumOfArguemntsException, IOException
    {
        var splitedInput = Utils.splitInputArguments(input, 1); //var not used, just checking num of arguments

        var fileList = Files.list(Paths.get(currentPath)).collect(Collectors.toList());
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

    }

    public void cat(String input, String currentPath) throws Exception
    {
        var splitedInput = Utils.splitInputArguments(input);
        File file = FileUtil.getFileIfExists(currentPath + File.separator + Utils.replaceFileSeparator(splitedInput[1]));
        if (file != null && !file.isDirectory())
        {
            var encryptedData = Files.readAllBytes(file.toPath());
            String key = CommandHandler.getKeyFromUser();
            var decryptedData = symmetricEncryption.decrypt(key, encryptedData);
            System.out.println(new String(decryptedData));
        } else
            throw new FileNotFoundException();
    }

    public void rm(String input, String currentPath) throws InvalidNumOfArguemntsException, IOException
    {
        var splitedInput = Utils.splitInputArguments(input);
        String pathString = currentPath + File.separator + Utils.replaceFileSeparator(splitedInput[1]);
        Path path = Paths.get(pathString);
        if (!Files.exists(path))
            throw new FileNotFoundException();

        var isDone = Files.deleteIfExists(path);
        if(isDone)
            System.out.println("Deleted");
        else
            System.out.println("Not deleted");
    }

    public void cls()
    {
        Utils.clearScreen();
    }
}
