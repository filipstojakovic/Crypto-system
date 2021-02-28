package crypto.exception;

public class InvalidNumOfArguemntsException extends Exception
{
    private static final String INVALID_NUM_OF_ARGS = "invalid num of arguemtns";

    public InvalidNumOfArguemntsException()
    {
        super(INVALID_NUM_OF_ARGS);
    }
}
