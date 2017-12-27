package pro.kretov.parser;

import java.util.Map;

public class Result {

    private Map<String, Integer> unique;

    public Result(Map<String, Integer> unique) {
        this.unique = unique;
    }

    public Map<String, Integer> getUnique() {
        return unique;
    }
}
