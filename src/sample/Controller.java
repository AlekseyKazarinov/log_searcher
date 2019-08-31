package sample;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.TouchEvent;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.File;
import java.io.IOException;
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
        System.out.println("Вошёл в метод");
        System.out.println("fileExtension = "+ fileExtension);
        Path startPath = Paths.get(dir);
        Stream<File> stream = Files.walk(startPath)
                .filter(path -> !Files.isDirectory(path))
                .map(path -> path.toFile())
                .filter(file-> file.getAbsolutePath().endsWith(fileExtension))
                .filter(file -> {
                    try {
                        return FileSearch.containsWord(file.getAbsolutePath(), text);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .peek(System.out::println);

        List<File> list = stream.collect(Collectors.toList());
        Iterator<File> it = list.iterator();
        File file;
        System.out.println("Перешёл к итератору");
        boolean isAppropriateFile;
        while (it.hasNext()) {
            file = it.next();
            if (file.canRead()) {
                System.out.println(file.getAbsolutePath());

                isAppropriateFile = FileSearch.containsWord(file.getAbsolutePath(), text);
                if (isAppropriateFile) {
                    System.out.println("подошёл " + file.getAbsolutePath());
                }

            }
        }

        return null;
    }


    @FXML
    public void clickSearch(ActionEvent event) {
        if (!checkFileExtensionField(fileExtensionField)) {
            return;
        };

        String textToSearch = textToSearchArea.getText();
        if (textToSearch.length() > 0 ) {
            String text = textToSearchArea.getText();
            infoLabel.setText("Идёт поиск текста \"" + text.substring(0, Math.min(text.length(), 20))+ "...\"");
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


    }
}
