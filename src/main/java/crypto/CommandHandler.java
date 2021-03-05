package crypto;

import crypto.encrypdecrypt.*;
import crypto.exception.FileAlteredException;
import crypto.exception.InvalidNumOfArguemntsException;
import crypto.jsonhandler.JsonSignatureHandler;
import crypto.user.User;
import crypto.utils.Constants;
import crypto.utils.FileUtil;
import crypto.utils.PrintUtil;
import crypto.utils.Utils;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.stream.Collectors;

import static crypto.MainApp.scanner;

public class CommandHandler
{
    private final User user;
    private final SymmetricEncryption symmetricEncryption;
    private final JsonSignatureHandler signatureHandler;

    public CommandHandler(User user)
    {
        this.user = user;
        symmetricEncryption = user.getSymmetricEncryption();
        signatureHandler = new JsonSignatureHandler(user);
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

        X509Certificate userCert = user.getX509Certificate();
        CertificateUtil.isCertValid(userCert);

        String pathString = currentPath + File.separator + Utils.replaceFileSeparator(splitInput[1]);
        Files.createFile(Paths.get(pathString));

        String content = getContentFromUser();
        String key = getKeyFromUser();

        var json = signatureHandler.createSignatureWithEncryptedContent(content.getBytes(StandardCharsets.UTF_8), key, KeyPairUtil.loadUserPublicKey(userCert));
        Files.writeString(Paths.get(pathString), Utils.prittyJson(json));
    }

    public void open(String input, String currentPath) throws Exception
    {
        var splitInput = Utils.splitInputArguments(input);
        String originalPathString = currentPath + File.separator + Utils.replaceFileSeparator(splitInput[1]);
        Path orginalPath = Paths.get(originalPathString);
        if (Files.isDirectory(orginalPath) || !Files.exists(orginalPath))
            throw new FileNotFoundException();

        var jsonFile = signatureHandler.loadFileContent(orginalPath);
        String symmetricKey = CommandHandler.getKeyFromUser();
        if (signatureHandler.verifySignature(jsonFile, symmetricKey, user.getKeyPair().getPrivate()))
            throw new FileAlteredException();

        Path copyPath = copyFilePath(originalPathString, orginalPath);
        FileUtils.copyFile(orginalPath.toFile(), copyPath.toFile());

        var content = symmetricEncryption.decrypt(symmetricKey, signatureHandler.getContentFromJSON(jsonFile));
        Files.write(copyPath, content);
        Desktop.getDesktop().open(copyPath.toFile());   //open copy file

        PrintUtil.printColorful("Save file content? (Y\\N): ", PrintUtil.ANSI_GREEN);
        String userInput = scanner.readLine().toLowerCase().trim();
        if ("y".equals(userInput) || "yes".equals(userInput))
        {
            var data = Files.readAllBytes(copyPath);
            var jsonSignature = signatureHandler.createSignatureWithEncryptedContent(data,symmetricKey,user.getKeyPair().getPublic());
            Files.writeString(orginalPath, Utils.prittyJson(jsonSignature));
        }
        Files.delete(copyPath);
    }

    public static String getKeyFromUser() throws IOException
    {
        PrintUtil.printColorful("Please enter file key: ", PrintUtil.ANSI_YELLOW);
        return scanner.readLine().trim();
    }

    private Path copyFilePath(String pathString, Path path)
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
            var jsonFile = signatureHandler.loadFileContent(file.toPath());
            CertificateUtil.isCertValid(user.getX509Certificate());
            String key = CommandHandler.getKeyFromUser();
            if(signatureHandler.verifySignature(jsonFile,key,user.getKeyPair().getPrivate()))
                throw new FileAlteredException();

            var decryptedData = symmetricEncryption.decrypt(key, signatureHandler.getContentFromJSON(jsonFile));
            System.out.println(new String(decryptedData));
        } else
            throw new FileNotFoundException();
    }

    public void dog(String input, String currentPath) throws InvalidNumOfArguemntsException, IOException
    {
        var splitedInput = Utils.splitInputArguments(input);
        File file = FileUtil.getFileIfExists(currentPath + File.separator + Utils.replaceFileSeparator(splitedInput[1]));
        if (file != null && !file.isDirectory())
        {
            var fileContent = Files.readAllBytes(file.toPath());
            System.out.println(new String(fileContent));
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
        if (isDone)
            System.out.println("Deleted");
        else
            System.out.println("Not deleted");
    }

    public void users(String input) throws InvalidNumOfArguemntsException, IOException
    {
        var splitedInput = Utils.splitInputArguments(input, 1);
        var fileList = Files.list(Paths.get(Constants.USER_DIR)).collect(Collectors.toList());
        fileList.stream()
                .filter(file -> file.toFile().isDirectory())
                .forEach(dir -> PrintUtil.printlnColorful(dir.getFileName().toString(), PrintUtil.ANSI_YELLOW));
    }

    public void upload(String input, String currentPath) throws Exception
    {
        var splitedInput = Utils.splitInputArguments(input);
        Path desktopFile = Paths.get(System.getProperty("user.home"), "Desktop", Utils.replaceFileSeparator(splitedInput[1]));

        if (!Files.exists(desktopFile) || Files.isDirectory(desktopFile))
            throw new FileNotFoundException();

        CertificateUtil.isCertValid(user.getX509Certificate());
        String key = getKeyFromUser();
        var fileContent = Files.readAllBytes(desktopFile);
        var jsonFile = signatureHandler.createSignatureWithEncryptedContent(fileContent,key,user.getKeyPair().getPublic());

        Path userFilePath = Paths.get(currentPath, desktopFile.getFileName().toString());
        Files.writeString(userFilePath, Utils.prittyJson(jsonFile));
    }

    public void download(String input, String currentPath) throws Exception
    {
        var splitedInput = Utils.splitInputArguments(input);
        String pathString = currentPath + File.separator + Utils.replaceFileSeparator(splitedInput[1]);
        Path filePath = Paths.get(pathString);
        if (!Files.exists(filePath) || Files.isDirectory(filePath))
            throw new FileNotFoundException();

        CertificateUtil.isCertValid(user.getX509Certificate());
        String key = getKeyFromUser();
        var jsonFile = signatureHandler.loadFileContent(filePath);

        if(signatureHandler.verifySignature(jsonFile,key,user.getKeyPair().getPrivate()))
            throw new FileAlteredException();

        var decrypedData = symmetricEncryption.decrtpyToString(key, signatureHandler.getContentFromJSON(jsonFile));

        Path desktopPath = Paths.get(System.getProperty("user.home"), "Desktop", filePath.getFileName().toString());
        Files.writeString(desktopPath, decrypedData);
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
        PrintUtil.printlnColorful("enter file content and \"exit\" for saving", PrintUtil.ANSI_YELLOW);
        while (!(line = scanner.readLine()).equals("exit"))
            stringBuilder.append(line).append("\n");

        if (!stringBuilder.isEmpty())
            stringBuilder.setLength(stringBuilder.length() - 1); // trim last space
        return stringBuilder.toString();
    }

}
