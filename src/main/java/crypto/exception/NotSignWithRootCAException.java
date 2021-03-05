package crypto.exception;

public class NotSignWithRootCAException extends Exception
{
    private static final String MSG = "Certificate not signed with RootCA";

    public NotSignWithRootCAException()
    {
        super(MSG);
    }
}
