package Utilities;

import java.util.ArrayList;
import com.google.gson.*;

public class test {
    static class Test {
        ArrayList<String> a = new ArrayList<>();
        public Test() {
            this.a.add("asd");
            this.a.add("ds");
        }
    }
    public static void main(String[] args) {

        //Test test = new Test();
        //Gson gson = new Gson();
        ArrayList<String> a = new ArrayList<String>();
        a.add("a");
        a.add("bcd");
        System.out.println(a);
    }
}
