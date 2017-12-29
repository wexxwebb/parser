package pro.kretov.resoucer.founder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ResListParser implements Founder {

    private List<String> resList;
    private String fileName;

    public ResListParser(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<String> getResourceList() throws IOException {
        this.resList = Files.readAllLines(Paths.get(fileName));
        return resList;
    }
}
