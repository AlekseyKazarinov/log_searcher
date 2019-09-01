package sample;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.stage.DirectoryChooser;


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


    private Stage mainStage = Main.mainStage;

    @FXML
    public void clickSelectDir(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(mainStage);
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
            infoLabel.setText("Установлено расширение файла: " + input);
            return true;
        } else {
            fileExtensionField.setText(FileExtension.DEFAULT_EXTENSION);
            infoLabel.setText("Неправильное расширение. Установлено исходное.");
            return false;
        }
    }


    /**
     * Построение дерева по файлам, в которых есть искомый текст.
     * @param treeFiles дерево файлов, которое будет строиться
     * @param text искомый текст
     * @param dir абсолютный путь каталога, в котором будут искаться файлы.
     * @param fileExtension расширение файлов в дереве
     */
    private void search(TreeView<PathItem> treeFiles, String text, String dir, String fileExtension) {
        Path startPath = Paths.get(selectedDirArea.getText());
        TreeItem<PathItem> root = new TreeItem<>(new PathItem(startPath));
        try {
            FileSearch.createTreeRoot(root, fileExtensionField.getText(), text);
            infoLabel.setText("Поиск файлов завершён.");
        } catch (Exception e ) {
            System.out.println(e.toString());
            infoLabel.setText("Произошла ошибка при поиске файлов.");
        }
        treeFiles.setRoot(root);
    }

    /**
     * Проверяет правильность введённых пользователем данных
     * @param textToSearchArea поле с текстом, который ищется
     * @param selectedDirArea поле, в котором хранится выбранный каталог
     * @param fileExtensionField поле с расширением файлов, по которым осуществляется поиск
     * @return удовлетворяет условиям или нет
     */
    private boolean checkInputFields(TextInputControl textToSearchArea,
                                     TextInputControl selectedDirArea,
                                     TextInputControl fileExtensionField) {
        int count = 0;
        // поле 1
        if (checkFileExtensionField(fileExtensionField)) {
            count++;
        }

        // поле 2
        String textToSearch = textToSearchArea.getText();
        if (textToSearch.length() > 0 ) {
            count++;
        } else {
            infoLabel.setText("Текст, который требуется найти, не введён.");
        }

        // поле 3
        if (selectedDirArea.getText().length() != 0) {
            count++;
        } else {
            infoLabel.setText("Не выбран каталог, в котором будем искать.");
        }

        // если все поля заполнены
        if (count == 3) {
            String text = textToSearchArea.getText();
            infoLabel.setText("Идёт поиск текста \""
                    + text.substring(0, Math.min(text.length(), 20))
                    + (text.length() > 20 ? "...\"" : "\""));
            return true;
        } else {
            return false;
        }
    }


    @FXML
    public void clickSearch(ActionEvent event) {
        boolean inputIsRight = checkInputFields(textToSearchArea, selectedDirArea, fileExtensionField);
        if (inputIsRight) {
            search(treeFiles, textToSearchArea.getText(), selectedDirArea.getText(), fileExtensionField.getText());
            flowPane.getChildren().clear();
            flowPane.getChildren().add(treeFiles);
        }
    }



    /**
     * Загружает файл из узла item в поле input
     * @param item узел TreeItem, в котором содержится файл
     * @param input поле вывода содержимого файла
     * @param infoLabel метка для вывода сообщений
     */
    private void loadFile(TreeItem<PathItem> item, TextInputControl input, Label infoLabel) {
        try {
            Path path = item.getValue().getPath();
            if (!Files.isDirectory(path)) {
                input.clear();
                contextLabel.setText("Содержимое файла " + path + ":");
                try {
                    FileInputStream fis = new FileInputStream(path.toFile());
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(fis))) {
                        String str;
                        while ((str = in.readLine()) != null) {
                            input.appendText(str+"\n");
                        }
                    } catch (IOException e) {
                        infoLabel.setText("Ошибка при чтении файла " + path.toString());
                    }
                } catch (FileNotFoundException e) {
                    infoLabel.setText("Файла "+ path.toString() +" не существует.");
                }
            } else {
                if (item.isExpanded()) {
                    item.setExpanded(false);
                } else {
                    item.setExpanded(true);
                }
            }
        } catch (NullPointerException e) {
            // ignore
        }

    }

    @FXML
    public void loadFileMouseClicked(MouseEvent mouseEvent) {
        TreeItem<PathItem> selectedItem = treeFiles.getSelectionModel().getSelectedItem();
        loadFile(selectedItem, contextArea, infoLabel);
    }

    @FXML
    public void textSelectionButton(ActionEvent event) {
        contextArea.selectAll();
    }
}
