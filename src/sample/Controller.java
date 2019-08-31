package sample;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.stage.DirectoryChooser;
import org.w3c.dom.ls.LSOutput;

class InputGUIException extends RuntimeException{

}

public class Controller {
    public FlowPane flowPane;
    public TextArea contextArea;
    public Label contextLabel;
    @FXML
    Label label1, label2, label3, label4, infoLabel;
    @FXML
    Button buttonSelectDir;
    @FXML
    Button searchButton;
    @FXML
    TextArea selectedDirArea, textToSearchArea;
    @FXML
    TextField fileExtensionField;
    @FXML
    TreeView<PathItem> treeFiles;

    SelectionModel<TreeItem<PathItem>> selectionModel;


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
                Path some = path.getName(k);
                System.out.println(some.toString());
            }
        }

        return null;
    }


    public static void createTree(TreeItem<PathItem> rootItem, String fileExtension, String text) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootItem.getValue().getPath())) {

            for (Path path : directoryStream) {

                TreeItem<PathItem> newItem = new TreeItem<PathItem>(new PathItem(path));



                if (Files.isDirectory(path)) {
                    rootItem.getChildren().add(newItem);
                    createTree(newItem, fileExtension, text);
                    newItem.setExpanded(false);
                }
                System.out.println(path.getFileName());
                if (!Files.isDirectory(path)){
                    File file = path.toFile();
                    System.out.println("not dir - "+path.getFileName()+ " " + file.getAbsolutePath());
                    if (file.getAbsolutePath().endsWith(fileExtension)) {
                        System.out.println("okay - "+path.getFileName());
                        boolean contains;
                        try {
                            contains = FileSearch.containsWord(file.getAbsolutePath(), text);
                        } catch (IOException e) {
                            e.printStackTrace();
                            contains = false;
                        }
                        if (contains) {
                            System.out.println("найден - " + file.getAbsolutePath());
                            rootItem.getChildren().add(newItem);
                        }
                    }
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
                    + (text.length() >20 ? "...\"" : "\""));
        } else {
            infoLabel.setText("Текст, который требуется найти, не введён.");
            return;
        }

        if (selectedDirArea.getText().length() == 0) {
            infoLabel.setText("Не выбран каталог, в котором будем искать.");
            return;
        }

        /*try {
            treeFiles = search(textToSearchArea.getText(), selectedDirArea.getText(), fileExtensionField.getText());
        } catch (IOException e) {
            infoLabel.setText("Ошибка при поиске файлов.");
            System.out.println(e);
        }*/

        Path startPath = Paths.get(selectedDirArea.getText());
        TreeItem<PathItem> root = new TreeItem<>(new PathItem(startPath));
        try {
            createTree(root, fileExtensionField.getText(), textToSearch);
            infoLabel.setText("Поиск файлов завершён.");
        } catch (Exception e ) {
            System.out.println(e);
            infoLabel.setText("Произошла ошибка при поиске файлов.");
        }
        //treeFiles = new TreeView<>(root);
        treeFiles.setRoot(root);
        if (flowPane.getChildren().size() > 0) {
            flowPane.getChildren().clear();
        }
        flowPane.getChildren().add(treeFiles);
    }


    private void loadFile() {
        TreeItem<PathItem> item = treeFiles.getSelectionModel().getSelectedItem();
        try {
            Path path = item.getValue().getPath();
            if (!Files.isDirectory(path)) {
                contextArea.clear();
                contextLabel.setText("Содержимое файла " + path + ":");
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(new FileInputStream(path.toFile()), "UTF-8"))) {
                    String str;
                    while ((str = in.readLine()) != null) {
                        contextArea.appendText(str+"\n");
                    }
                } catch (IOException e) {
                    infoLabel.setText("Файла "+ path.toString() +" не существует.");
                }
            } else {
                item.setExpanded(true);
            }
        } catch (NullPointerException exc) {  // корневой узел не содержит path
            // ignore
        }
    }

    @FXML
    public void loadSelectedItem(ActionEvent event) {
        loadFile();
    }

    @FXML
    public void loadFileMouseClicked(MouseEvent mouseEvent) {
        loadFile();
    }
}
