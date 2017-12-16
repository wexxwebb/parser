package pro.kretov;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FileFounder implements Founder {

    Path path;
    String extension;
    List<String> fileList;

    @Override
    public List<String> getResList() {
        try {
            Files.walkFileTree(path, new SFV());
        } catch (IOException e) {
            System.out.println("Can't build path tree!");
        }
        return fileList;
    }

    private class SFV extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
            if (path.getFileName().toString().endsWith(extension)) {
                fileList.add(path.toAbsolutePath().toString());
            }
            return CONTINUE;
        }
    }

    public FileFounder(String pathName, String extension) {
        this.extension = extension;
        path = Paths.get(pathName);
        fileList = new ArrayList<>();
    }
    
}
