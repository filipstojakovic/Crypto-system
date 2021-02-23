package crypto.utils;

import java.io.File;

public class FileUtil
{
    public static File getFileIfExists(String input)
    {
        File file = new File(input);
        return (file.exists() && file.isFile()) ? file : null;
    }

}
