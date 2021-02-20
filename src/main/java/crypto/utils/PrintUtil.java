package crypto.utils;

public class PrintUtil
{
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String RESET = "\033[0m";

    public static void printColorful(String msg)
    {
        System.out.print(ANSI_GREEN + msg + RESET);
    }

    public static void printlnColorful(String msg)
    {
        System.out.println(ANSI_GREEN + msg + RESET);
    }
}
