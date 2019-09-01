package sample;

import javafx.scene.control.TreeItem;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Класс, организующий поиск текста в файлах.
 */
public class FileSearch {

    public static boolean containsWord(String fileName, String word) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName))).contains(word);
    }

    private static boolean hasText(String filename, String text) {
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

    /**
     * Рекурсивная функция, создающая дерево из файлов, которые удовлетворяют условиям
     * @param rootItem корневой узел, от которого будем строить дерево каталогов и файлов
     * @param fileExtension расширение файлов в дереве
     * @param text текст, который в них должен содержаться
     */
    static void createTreeRoot(TreeItem<PathItem> rootItem, String fileExtension, String text) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootItem.getValue().getPath())) {

            for (Path path : directoryStream) {

                TreeItem<PathItem> newItem = new TreeItem<>(new PathItem(path));

                if (Files.isDirectory(path)) {
                    rootItem.getChildren().add(newItem);
                    newItem.setExpanded(false);
                    createTreeRoot(newItem, fileExtension, text);
                    // если в данном каталоге не оказалось подходящих файлов:
                    if (newItem.getChildren().size() == 0) {
                        rootItem.getChildren().remove(newItem);
                    }
                }

                if (!Files.isDirectory(path)){
                    File file = path.toFile();
                    if (file.getAbsolutePath().endsWith(fileExtension)) {
                        if (FileSearch.hasText(file.getAbsolutePath(), text)) {
                            rootItem.getChildren().add(newItem);
                        }
                    }
                }
            }
        } catch (IOException e) {
            // ignore
        }
    }
}

