public class Utils
{

    public static final int LINE_NUM = 55;
    public static final String REGEX_SPACES = "\\s";

    //dummy clear
    public static void clearScreen()
    {
        for (int i = 0; i < LINE_NUM; i++)
        {
            System.out.println();
        }
    }
}
