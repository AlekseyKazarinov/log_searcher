package sample;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Класс, организующий поиск текста в файлах.
 */
public class FileSearch {

    public static boolean containsWord(String fileName, String word) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName))).contains(word);
    }

    static boolean hasText(String filename, String text) {
        boolean hasText = false;
        byte[] bytes = text.getBytes();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(filename))))) {
            int count = 0;
            byte symbol = (byte) br.read();
            while (symbol != -1) {    // Когда дойдём до конца файла, получим '-1'
                if (symbol == bytes[count]) {
                    count++;
                    if (count == bytes.length) {
                        hasText = true;
                        break;
                    }
                } else {
                    count = 0;
                }

                symbol = (byte) br.read(); // Читаем байт
            }
        } catch (IOException e) {
            // ignored
        }
        return hasText;
    }
}

