package crypto.exception;

public class NotForYouException extends Exception
{
    private static final String MSG = "file is not shared with you";

    public NotForYouException()
    {
        super(MSG);
    }
}
