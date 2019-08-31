package sample;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.TouchEvent;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.stage.DirectoryChooser;
import org.w3c.dom.ls.LSOutput;

class InputGUIException extends RuntimeException{

}

public class Controller {
    @FXML
    Label label1, label2, label3, label4, label5, infoLabel;
    @FXML
    Button buttonSelectDir;
    @FXML
    Button searchButton;
    @FXML
    TextArea selectedDirArea, textToSearchArea;
    @FXML
    TextField fileExtensionField;
    @FXML
    TreeView<Path> treeFiles;


    Stage mainStage = Main.mainStage;
    Stage selectDirStage;
    //final FileChooser fileChooser = new FileChooser();
    final DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML
    public void clickSelectDir(ActionEvent event) {
        //textFieldSelectedDir.clear();
        File dir = directoryChooser.showDialog(selectDirStage);
        if (dir != null) {
            selectedDirArea.setText(dir.getAbsolutePath());
        } else {
            selectedDirArea.setText(null);
        }
    }


    private boolean checkFileExtensionField(TextInputControl fileExtensionField) {
        String input = fileExtensionField.getText();
        boolean isExtension = FileExtension.check(input);
        if (isExtension) {
            infoLabel.setText(String.format("Установлено расширение файла: {0}", input));
            return true;
        } else {
            infoLabel.setText("Неправильный формат расширения. Установлено исходное.");
            fileExtensionField.setText(FileExtension.DEFAULT_EXTENSION);
            return false;
        }
    }

    @FXML
    public void onTextFileTypeExited(TouchEvent event) {
        infoLabel.setText("mouse event");
    }

    /**
     * Построение дерева по файлам, в которых есть искомый текст.
     * @param text искомый текст
     * @param dir абсолютный путь каталога, в котором будут искаться файлы.
     * @return дерево файлов, содержащих заданный текст.
     */
    public TreeView<Path> search(String text, String dir, String fileExtension) throws IOException {
        //System.out.println("Вошёл в метод");
        //System.out.println("fileExtension = "+ fileExtension);
        Path startPath = Paths.get(dir);
        Stream<Path> stream = Files.walk(startPath)
                .filter(path -> !Files.isDirectory(path))
                .map(path -> path.toFile())
                .filter(file-> file.getAbsolutePath().endsWith(fileExtension))
                .filter(file -> file.canRead())
                .filter(file -> {
                    try {
                        return FileSearch.containsWord(file.getAbsolutePath(), text); // ищет только в ASCII :(
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .map(file -> file.toPath())
                .peek(System.out::println);
        List<Path> list = stream.collect(Collectors.toList());
        TreeView<Path> treeView = new TreeView<>(new TreeItem<>(startPath));  // строим дерево от каталога, в котором ищем
        int index = startPath.getNameCount();
        for (Path path : list) {
            for (int k = index; k < path.getNameCount(); k++) {
                path.getName(k);
            }
        }

        return null;
    }


    public static void createTree(TreeItem<Path> rootItem) throws IOException {

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootItem.getValue())) {

            for (Path path : directoryStream) {

                TreeItem<Path> newItem = new TreeItem<Path>(path);
                newItem.setExpanded(true);

                rootItem.getChildren().add(newItem);

                if (Files.isDirectory(path)) {
                    createTree(newItem);
                }
            }
        }
    }

    @FXML
    public void clickSearch(ActionEvent event) {
        if (!checkFileExtensionField(fileExtensionField)) {
            return;
        };

        String textToSearch = textToSearchArea.getText();
        if (textToSearch.length() > 0 ) {
            String text = textToSearchArea.getText();
            infoLabel.setText("Идёт поиск текста \""
                    + text.substring(0, Math.min(text.length(), 20))
                    + (text.length() >20 ? "...\"" : ""));
        } else {
            infoLabel.setText("Текст, который требуется найти, не введён.");
            return;
        }

        if (selectedDirArea.getText().length() == 0) {
            infoLabel.setText("Не выбран каталог, в котором будем искать.");
            return;
        }

        try {
            treeFiles = search(textToSearchArea.getText(), selectedDirArea.getText(), fileExtensionField.getText());
        } catch (IOException e) {
            infoLabel.setText("Ошибка при поиске файлов.");
            System.out.println(e);
        }

        Path startPath = Paths.get(selectedDirArea.getText());
        TreeItem<Path> root = new TreeItem<>(startPath);
        try {
            createTree(root);
        } catch (Exception e ) {
            // ignore
        }
        treeFiles = new TreeView<>(root);

    }
}
