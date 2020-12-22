package Utilities;

public class ColorPrint {
    enum Color {
        BLACK, RED, GREEN, YELLOW, BLUE, VIOLET, SKYBLUE, WHITE, GREY;
    }
    public void printWithColor(String s, Color color) {
        System.out.println("\033[" + (90 + color.ordinal()) + ";1m" + s + "\033[0m");
    }
    public String addColor(String s, Color color) {
        return "\033[" + (90 + color.ordinal()) + ";1m" + s + "\033[0m";
    }
    public static void main(String[] args) {
        ColorPrint print = new ColorPrint();
        print.printWithColor("hello world", Color.SKYBLUE);
    }

}
