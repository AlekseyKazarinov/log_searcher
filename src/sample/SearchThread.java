package sample;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Поток, организующий поиск текста в файлах.
 */

public class SearchThread extends Thread {

    private TreeView<PathItem> treeFiles;
    private String fileExtension;
    private String text;
    private String dir;

    SearchThread(String name,
                 TreeView<PathItem> treeFiles,  // дерево файлов, которое будет строиться
                 String fileExtension,          // расширение файлов в дереве
                 String text,                   // искомый текст
                 String dir) {                  // абсолютный путь каталога, в котором будут искаться файлы
        super(name);
        this.treeFiles = treeFiles;
        this.fileExtension = fileExtension;
        this.text = text;
        this.dir = dir;
    }

    /*public static boolean containsWord(String fileName, String word) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName))).contains(word);
    }*/

    private static boolean hasText(String filename, String text) {
        boolean hasText = false;
        byte[] bytes = text.getBytes();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(filename))))) {
            int count = 0;
            int symbol = br.read();
            while (symbol != -1) {    // Когда дойдём до конца файла, получим '-1'
                if ((byte) symbol == bytes[count]) {
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
     *
     * @param rootItem      корневой узел, от которого будем строить дерево каталогов и файлов
     * @param fileExtension расширение файлов в дереве
     * @param text          текст, который в них должен содержаться
     */
    private static void createTreeRoot(TreeItem<PathItem> rootItem, String fileExtension, String text) {
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

                if (!Files.isDirectory(path)) {
                    File file = path.toFile();
                    if (file.getAbsolutePath().endsWith(fileExtension)) {
                        if (hasText(file.getAbsolutePath(), text)) {
                            rootItem.getChildren().add(newItem);
                        }
                    }
                }
            }
        } catch (IOException e) {
            // ignore
        }
    }


    private void search() {
        Path startPath = Paths.get(this.dir);
        TreeItem<PathItem> root = new TreeItem<>(new PathItem(startPath));
        try {
            createTreeRoot(root, this.fileExtension, this.text);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        if (!Thread.currentThread().isInterrupted()) {
            root.setExpanded(true);
            // т.к. дочерний поток не может напрямую изменять элементы интерфейса javafx:
            Platform.runLater(() -> this.treeFiles.setRoot(root));
        }
    }

    @Override
    public void run() {
        System.out.printf("Поток с именем %s начал свою работу.\n", Thread.currentThread().getName());
        this.search();
        if (Thread.currentThread().isInterrupted()) {
            System.out.printf("Поток с именем %s прерван.\n", Thread.currentThread().getName());
        } else {
            System.out.printf("Поток с именем %s завершил работу успешно.\n", Thread.currentThread().getName());
        }
    }

}
