package sample.activity;

import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Класс, описывающий каталоги и файлы, которые могут храниться в TreeItem
 */
public class PathItem {
    private String fullPath;
    private String name;

    PathItem(Path path) {
        this.fullPath = path.toString();
        this.name = path.getFileName().toString();
    }

    public String getPathName() {
        return this.fullPath;
    }

    public Path getPath() {
        return Paths.get(fullPath);
    }

    @Override
    public String toString() {
        return name;
    }
}
