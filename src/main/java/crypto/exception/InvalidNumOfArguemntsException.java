package crypto.exception;

public class InvalidNumOfArguemntsException extends Exception
{
    private static final String INVALID_NUM_OF_ARGS = "Invalid num of arguemtns";

    public InvalidNumOfArguemntsException()
    {
        super(INVALID_NUM_OF_ARGS);
    }
}
