package sample.activity;

import java.io.*;

public class Loader {

    public static String loadFromFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(fis))) {
            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str).append("\n");
            }
        }
        return sb.toString();
    }

}
