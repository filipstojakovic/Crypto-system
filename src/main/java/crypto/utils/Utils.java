package crypto.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Utils
{
    public static final long DAY_IN_MILLS = 1000L * 60 * 60 * 24;
    public static final long THIRTY_DAYS = DAY_IN_MILLS * 30;
    public static final long YEAR_IN_MILLS = 365 * DAY_IN_MILLS;

    public static final int LINE_NUM = 55;
    public static final String REGEX_SPACES = "\\s";

    public static String bytesToHex(byte[] hash)
    {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++)
        {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1)
            {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    //dummy clear
    public static void clearScreen()
    {
        for (int i = 0; i < LINE_NUM; i++)
        {
            System.out.println();
        }
    }

    public static File getFileFromResource(String fileName) throws URISyntaxException
    {

        Path resourceDirectory = Paths.get("src", "main", "resources", fileName);
        String path = resourceDirectory.toString();
        return FileUtil.createFileIfNeeded(path);
    }

    public static File getFileFromResource(String fileName, String fileExtension) throws URISyntaxException, FileNotFoundException
    {
        return getFileFromResource(fileName + fileExtension);
    }
}
