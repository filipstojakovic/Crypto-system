package crypto.user.exceptions;

public class InvalidNumOfArguemntsException extends Exception
{
    public static final String INVALID_NUM_OF_ARGS = "invalid num of arguemtns";

    public InvalidNumOfArguemntsException()
    {
        super(INVALID_NUM_OF_ARGS);
    }
}
