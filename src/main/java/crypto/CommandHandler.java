package crypto;

import crypto.encrypdecrypt.CertificateUtil;
import crypto.encrypdecrypt.HashUtil;
import crypto.encrypdecrypt.KeyPairUtil;
import crypto.encrypdecrypt.SymmetricEncryption;
import crypto.exception.*;
import crypto.jsonhandler.JsonSignatureHandler;
import crypto.user.User;
import crypto.utils.Constants;
import crypto.utils.FileUtil;
import crypto.utils.PrintUtil;
import crypto.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.stream.Collectors;

import static crypto.MainApp.scanner;
import static crypto.encrypdecrypt.SymmetricEncryption.AES;
import static crypto.jsonhandler.JsonSignatureHandler.*;

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

        var decryptedData = validateAndExtactContent(orginalPath);
        Path copyPath = copyFilePath(originalPathString, orginalPath);
        FileUtils.copyFile(orginalPath.toFile(), copyPath.toFile());

        Files.write(copyPath, decryptedData);
        Desktop.getDesktop().open(copyPath.toFile());   //open copy file

        PrintUtil.printColorful("Save file content? (Y\\N): ", PrintUtil.ANSI_GREEN);
        String userInput = scanner.readLine().toLowerCase().trim();
        if ("y".equals(userInput) || "yes".equals(userInput))
        {
            var data = Files.readAllBytes(copyPath);
            String symmetricKey = getKeyFromUser();
            var jsonSignature = signatureHandler.createSignatureWithEncryptedContent(data, symmetricKey, user.getKeyPair().getPublic());
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
        printFolderContent(Paths.get(currentPath));
    }

    private void printFolderContent(Path currentPath) throws IOException
    {
        var fileList = Files.list(currentPath).collect(Collectors.toList());
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
            var jsonFile = signatureHandler.loadJsonFileContent(file.toPath());
            CertificateUtil.isCertValid(user.getX509Certificate());
            var decryptedData = validateAndExtactContent(file.toPath());
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

    public List<String> users(String input) throws InvalidNumOfArguemntsException, IOException
    {
        var splitedInput = Utils.splitInputArguments(input, 1);
        List<String> userList = getAllUsers();
        userList.forEach(dir -> PrintUtil.printlnColorful(dir, PrintUtil.ANSI_YELLOW));
        return userList;
    }

    @NotNull
    private List<String> getAllUsers() throws IOException
    {
        var fileList = Files.list(Paths.get(Constants.USER_DIR)).collect(Collectors.toList());
        var userList = fileList.stream()
                .filter(file -> file.toFile().isDirectory())
                .map(file -> file.getFileName().toString())
                .collect(Collectors.toList());
        return userList;
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
        var jsonFile = signatureHandler.createSignatureWithEncryptedContent(fileContent, key, user.getKeyPair().getPublic());

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

        var decrypedData = validateAndExtactContent(filePath);

        Path desktopPath = Paths.get(System.getProperty("user.home"), "Desktop", filePath.getFileName().toString());
        Files.write(desktopPath, decrypedData);
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

    public void shareFileWith(String input, String currentPath) throws Exception
    {
        var splitedInput = Utils.splitInputArguments(input, 3);

        var filePath = currentPath + File.separator + Utils.replaceFileSeparator(splitedInput[1]);
        Path path = Paths.get(filePath);
        if (!Files.exists(path))
            throw new FileNotFoundException();

        var shareUsername = splitedInput[2];
        var userList = getAllUsers();
        if (userList.isEmpty() || !userList.contains(shareUsername))
            throw new NoUserException();

        X509Certificate shareUserCert = validateBothCerts(shareUsername);

        var fileContent = validateAndExtactContent(path);
        var fileName = path.getFileName().toString();

        var jsonSharedFile = signatureHandler.createSharedSignature(fileContent, shareUsername, shareUserCert);

        Path sharePath = Paths.get(Constants.SHARE_DIR, fileName);
        Files.writeString(sharePath, jsonSharedFile.toString());
    }

    @NotNull
    private X509Certificate validateBothCerts(String shareUsername) throws CertificateException, NotSignWithRootCAException, FileNotFoundException
    {
        CertificateUtil.isCertValid(user.getX509Certificate());
        var shareUserCert = CertificateUtil.loadUserCertificate(shareUsername);
        CertificateUtil.isCertValid(shareUserCert);
        return shareUserCert;
    }

    private byte[] validateAndExtactContent(Path path) throws Exception
    {
        var jsonFile = signatureHandler.loadJsonFileContent(path);
        String key = getKeyFromUser();
        if (signatureHandler.isSignatureAltered(jsonFile, key, user.getKeyPair().getPrivate()))
            throw new FileAlteredException();
        return symmetricEncryption.decrypt(key, signatureHandler.getContentFromJSON(jsonFile));
    }

    public void openSharedFile(String input) throws Exception
    {
        var splitInput = Utils.splitInputArguments(input);
        var sharedJson = signatureHandler.loadJsonFileContent(Paths.get(Constants.SHARE_DIR + splitInput[1]));

        String sender = signatureHandler.getSender(sharedJson);
        String reciver = signatureHandler.getReciever(sharedJson);

        if (!user.getUsername().equals(reciver))
            throw new NotForYouException();

        var shareUserCert = validateBothCerts(sender);
        var decryptedSymmKey = signatureHandler.extractDecryptedSymmKey(sharedJson, shareUserCert);
        var shareSignatureAndContent = signatureHandler.getShareContentJson(sharedJson);

        if (signatureHandler.isSignatureAltered(shareSignatureAndContent, AES, new String(decryptedSymmKey), SHARE_HASH_ALGO, shareUserCert.getPublicKey()))
            throw new FileAlteredException();

        var shareEncryptedContent = signatureHandler.getContentFromJSON(shareSignatureAndContent);
        var data = symmetricEncryption.decrypt(new String(decryptedSymmKey), shareEncryptedContent, AES);
        System.out.println(new String(data));
    }

    public void printShareFolder(String input) throws InvalidNumOfArguemntsException, IOException
    {
        var splitInput = Utils.splitInputArguments(input, 1);
        printFolderContent(Paths.get(Constants.SHARE_DIR));
    }
}
