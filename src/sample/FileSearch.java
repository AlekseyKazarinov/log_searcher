package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Класс, организующий поиск текста в файлах.
 */
public class FileSearch {
    private String text;
    //private Path file;

    public static boolean containsWord(String fileName, String word) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName))).contains(word);
    }


    public static boolean hasText(String filename, String text) throws IOException {
        System.out.println("has Text opened");
        char[] chars = text.toCharArray();
        System.out.println("chars length = " + chars.length);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            int symbol = bufferedReader.read();
            //char[] buffer = new char[chars.length];
            int count = 0;
            while (symbol != -1) {  // Когда дойдём до конца файла, получим '-1'
                System.out.println("Считан символ: "+ Character.toString((char) symbol));
                if ((char) symbol == chars[count]) {
                    //buffer[count] = (char) symbol;
                    count++;
                    System.out.println("count = " + count);
                    if (count == chars.length) {
                        return true;
                    }
                } else {
                    count = 0;
                }
                symbol = bufferedReader.read(); // Читаем символ
            }
        }
        return false;
    }
}

class ElementPath {
    private Path path;
    public ElementPath(Path path){
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }

    @Override
    public String toString() {
        return "";
    }
}
