package crypto.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;

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

    public static File getFileFromResource(String fileName) throws URISyntaxException, FileNotFoundException
    {

        ClassLoader classLoader = Utils.class.getClassLoader();
        URL resource = classLoader.getResource(fileName.trim());
        if (resource == null)
        {
            throw new FileNotFoundException("File not found! " + fileName);
        } else
        {

            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());
            return new File(resource.toURI());
        }
    }
}
