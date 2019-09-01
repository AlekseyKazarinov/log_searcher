package sample;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Exchanger;

public class SearchThread extends Thread {

    //private TreeItem<PathItem> rootItem;
    private TreeView<PathItem> treeFiles;
    private String fileExtension;
    private String text;
    private String dir;

    SearchThread(String name,
                 TreeView<PathItem> treeFiles,
                 String fileExtension,
                 String text,
                 String dir) {
        super(name);
        //this.exchanger = exchanger;
        //this.rootItem = rootItem;
        this.treeFiles = treeFiles;
        this.fileExtension = fileExtension;
        this.text = text;
        this.dir = dir;
    }

    /**
     * Построение дерева по файлам, в которых есть искомый текст.
     * @param treeFiles дерево файлов, которое будет строиться
     * @param text искомый текст
     * @param dir абсолютный путь каталога, в котором будут искаться файлы.
     * @param fileExtension расширение файлов в дереве
     */
    private void search(TreeView<PathItem> treeFiles, String text, String dir, String fileExtension) {
        Path startPath = Paths.get(dir);
        TreeItem<PathItem> root = new TreeItem<>(new PathItem(startPath));
        try {
            FileSearch.createTreeRoot(root,fileExtension, text);
            //FileSearch.createTreeRoot(root, fileExtensionField.getText(), text);
            //infoLabel.setText("Поиск файлов завершён.");
        } catch (Exception e ) {
            System.out.println(e.toString());
            //infoLabel.setText("Произошла ошибка при поиске файлов.");
        }

        if (!Thread.currentThread().isInterrupted()) {
            // т.к. дочерний поток не может напрямую изменять элементы интерфейса javafx:
            Platform.runLater( () -> treeFiles.setRoot(root) );
        }
    }

    @Override
    public void run() {
        System.out.printf("Поток с именем %s начал свою работу.\n",Thread.currentThread().getName());
        this.search(treeFiles, text, dir, fileExtension);

        System.out.printf("Поток с именем %s завершил работу успешно.\n",Thread.currentThread().getName());

    }

}
