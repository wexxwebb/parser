package pro.kretov.resoucer.builder;

import pro.kretov.resoucer.founder.Founder;
import pro.kretov.sleeper.Sleeper;
import pro.kretov.wrapper.Wrapper;

import java.io.IOException;
import java.util.List;

public class ResourceListFactory implements Builder {

    private Founder resourceListParser;

    public ResourceListFactory(Founder resourceListParser) {
        this.resourceListParser = resourceListParser;
    }

    private List<String> create(int retry) {
        try {
            return resourceListParser.getResourceList();
        } catch (IOException e) {
            retry++;
            if (retry > 5) return null;
            else if (retry == 1) System.out.print("Reading resource file.");
            else System.out.print(".");
            Sleeper.sleep(500);
            return create(retry);
        }
    }

    @Override
    public Wrapper<List<String>> build() {
        List<String> list;
        if ((list = create(0)) != null) {
            return new Wrapper<>(list, true, "Sucsess bulding resource list from file");
        } else {
            return new Wrapper<>(null, false, "Fail building resource list. Can't open or read file.");
        }
    }
}
