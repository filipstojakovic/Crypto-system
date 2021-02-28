package crypto.exception;

public class MessageAlteredException extends Exception
{
    private static final String MSG = "Message has been altered";

    public MessageAlteredException()
    {
        super(MSG);
    }
}
