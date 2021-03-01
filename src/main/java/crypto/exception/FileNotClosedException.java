package crypto.exception;

public class FileNotClosedException extends Exception
{
    private static final String MSG = "File is not closed";

    public FileNotClosedException()
    {
        super(MSG);
    }
}
