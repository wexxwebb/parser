package pro.kretov.parser;

import java.util.Map;
import java.util.Set;

public class Result {

    private Map<String, Integer> unique;
    private Set<String> nonUnique;

    public Result(Map<String, Integer> unique, Set<String> nonUnique) {
        this.unique = unique;
        this.nonUnique = nonUnique;
    }

    public Map<String, Integer> getUnique() {
        return unique;
    }

    public Set<String> getNonUnique() {
        return nonUnique;
    }
}
