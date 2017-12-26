package pro.kretov.founder.resFounder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ResListParser implements Founder {

    private List<String> resList;

    public ResListParser(String fileName) throws IOException {
        this.resList = Files.readAllLines(Paths.get(fileName));
    }

    @Override
    public List<String> getResourceList() {
        return resList;
    }
}
