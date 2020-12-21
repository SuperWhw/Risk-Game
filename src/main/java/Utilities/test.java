package Utilities;

public class test {
    public static void main(String[] args) {
        for(int i = 30; i <= 40; ++i) {
            System.out.println("\033[" + i + ";1m" + "hello world" + "\033[0m");
        }
        for(int i = 90; i <= 98; ++i) {
            System.out.println("\033[" + i + ";1m" + "hello world" + "\033[0m");
        }
        for(int i = 30; i <= 40; ++i) {
            System.out.println("\033[" + i + ";4m" + "hello world" + "\033[0m");
        }
        for(int i = 90; i <= 98; ++i) {
            System.out.println("\033[" + i + ";4m" + "hello world" + "\033[0m");
        }
    }
}

