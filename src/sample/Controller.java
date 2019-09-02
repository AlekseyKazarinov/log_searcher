package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.*;
import java.nio.file.*;

import javafx.stage.DirectoryChooser;
import sample.activity.FileExtension;
import sample.activity.Loader;
import sample.activity.PathItem;
import sample.activity.SearchThread;


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
    private Thread searcher;

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
     * Проверяет правильность введённых пользователем данных
     *
     * @param textToSearchArea   поле с текстом, который ищется
     * @param selectedDirArea    поле, в котором хранится выбранный каталог
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
        if (textToSearch.length() > 0) {
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
            flowPane.getChildren().clear();
            flowPane.getChildren().add(treeFiles);

            if (searcher != null) {
                searcher.interrupt();
            }

            searcher = new SearchThread("searcher",
                    treeFiles,
                    fileExtensionField.getText(),
                    textToSearchArea.getText(),
                    selectedDirArea.getText());
            searcher.setPriority(Thread.NORM_PRIORITY + 3);
            searcher.setDaemon(true);  // это обслуживающий поток
            searcher.start();
        }
    }


    /**
     * Загружает файл, соответствующий узлу PathItem, в поле context
     *
     * @param item      узел TreeItem, в котором содержится файл
     * @param context   поле вывода содержимого файла
     * @param infoLabel метка для вывода сообщений
     */
    private void loadFile(TreeItem<PathItem> item, TextInputControl context, Label contextLabel, Label infoLabel) {
        try {
            Path path = item.getValue().getPath();
            if (!Files.isDirectory(path)) {
                context.clear();
                contextLabel.setText("Содержимое файла " + path.getFileName() + ":");
                try {
                    context.setText(Loader.loadFromFile(path.toFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                    infoLabel.setText("Возникла шибка при чтении файла " + path.toString());
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
        loadFile(selectedItem, contextArea, contextLabel, infoLabel);
    }

    @FXML
    public void textSelectionButton(ActionEvent event) {
        contextArea.selectAll();
    }
}
