package Utilities;
import java.io.*;

public class FileIOBasics {
    public void writeFile(String filename, String content) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(content);
            fw.close();
        }
        catch(IOException e) {
            System.out.println("Failed to Write content into " + filename);
        }
    }
    public String readJsonFile(String filename) {
        try {
            FileReader fr = new FileReader(filename);
            Reader reader =  new InputStreamReader(new FileInputStream(filename),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fr.close();
            reader.close();
            return sb.toString();
        }
        catch(IOException e) {
            System.out.println("Failed to read file " + filename);
            return "";
        }
    }

}
