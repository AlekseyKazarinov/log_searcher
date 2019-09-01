package sample;

import javafx.scene.control.TreeView;
import javafx.scene.layout.FlowPane;

public class TreePainterThread extends Thread {

    private FlowPane flowPane;
    private TreeView<PathItem> treeFiles;
    private String fileExtension;
    private String textToSearch;
    private String selectedDir;

    TreePainterThread(String name,
                      FlowPane flowPane,
                      TreeView<PathItem> treeFiles,
                      String fileExtension,
                      String textToSearch,
                      String selectedDir) {
        super(name);
        this.flowPane = flowPane;
        this.treeFiles = treeFiles;
        this.fileExtension = fileExtension;
        this.textToSearch = textToSearch;
        this.selectedDir = selectedDir;
    }

    private void paint(){

        System.out.println("Painter ждёт.");
        System.out.printf("Painter закончил ждать.");
        flowPane.getChildren().clear();
        flowPane.getChildren().add(treeFiles);
        System.out.printf("Painter отрисовал дерево.");

    }

    public void  run() {
        SearchThread searcher = new SearchThread("searcher",
                treeFiles,
                fileExtension,
                textToSearch,
                selectedDir);

        try {
            searcher.join();
        } catch (InterruptedException e) {
            // ignore
        }
        searcher.start();
        paint();
    }
}
