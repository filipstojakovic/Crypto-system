package crypto.exception;

public class FileAlteredException extends Exception
{
    private static final String MSG = "File has been altered";

    public FileAlteredException()
    {
        super(MSG);
    }
}
