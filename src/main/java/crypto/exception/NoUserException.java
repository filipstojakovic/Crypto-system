package crypto.exception;

public class NoUserException extends Exception
{
    private static final String MSG = "No user exception";

    public NoUserException()
    {
        super(MSG);
    }
}
