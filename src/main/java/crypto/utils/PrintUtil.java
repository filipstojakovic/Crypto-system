package crypto.utils;

public class PrintUtil
{
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String RESET = "\033[0m";

    public static void printColorful(String msg)
    {
        System.out.print(ANSI_GREEN + msg + RESET);
    }

    public static void printlnColorful(String msg)
    {
        System.out.println(ANSI_GREEN + msg + RESET);
    }

    public static void printlnColorful(String msg, String color)
    {
        System.out.println(color + msg + RESET);
    }

    public static void printColorful(String msg, String color)
    {
        System.out.print(color + msg + RESET);
    }

}
