package crypto.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public abstract class Utils
{

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

    //    public static File getFileFromResource(String fileName) throws URISyntaxException
    //    {
    //        Path resourceDirectory = Paths.get("src", "main", "resources", fileName);
    //        String path = resourceDirectory.toString();
    //        return FileUtil.createFileIfNeeded(path);
    //    }
    //
    //    public static File getFileFromResource(String fileName, String fileExtension) throws URISyntaxException, FileNotFoundException
    //    {
    //        return getFileFromResource(fileName + fileExtension);
    //    }


    @NotNull
    public static String randomString(int length)
    {
        Random random = new Random();
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'

        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static Date getCurrentDate()
    {
        return new Date(System.currentTimeMillis() - 1000L * 5); // 5 sec before current date
    }

    public static Date getNextYearDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1); // to get previous year add -1
        return cal.getTime();
    }

    public static boolean checkForTwoArguments(String input)
    {
        return checkNumberOfArguments(input, 2);
    }

    public static boolean checkNumberOfArguments(String input, int numOfArgs)
    {
        var args = input.split(REGEX_SPACES);
        return (args != null && args.length == numOfArgs);
    }
}
