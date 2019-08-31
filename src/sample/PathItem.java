package sample;

import java.nio.file.Path;

public class PathItem {
    private Path path;
    PathItem(Path path) {
        this.path = path;
    }

    public Path getPath(){
        return this.path;
    }

    @Override
    public String toString() {
        return path.getFileName().toString();
    }
}
