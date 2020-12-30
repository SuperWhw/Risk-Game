package Utilities;
import java.io.*;
import java.nio.charset.StandardCharsets;

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
            InputStream is = this.getClass().getResourceAsStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            return br.readLine();
        }
        catch(IOException e) {
            System.out.println("Failed to read file " + filename);
            return "";
        }
    }


}
