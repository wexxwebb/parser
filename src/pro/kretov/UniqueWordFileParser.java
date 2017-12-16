package pro.kretov;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicWordFileParser implements Callable<Set<String>> {

    private String fileName;
    private String regExp;

    public UnicWordFileParser(String fileName, String regExp) {
        this.fileName = fileName;
        this.regExp = regExp;
    }

    @Override
    public Set<String> call() throws Exception {
        List<String> result = new ArrayList<>();
        Set<String> unicWords = new HashSet<>();
        Set<String> notUnicWords = new HashSet<>();

        String allFileString = new String(Files.readAllBytes(Paths.get(fileName)));
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(allFileString);
        while (matcher.find()) {
            StringBuilder sb = new StringBuilder(allFileString.substring(matcher.start(), matcher.end()));
            if (!notUnicWords.contains(sb.toString())) {
                if (unicWords.contains(sb.toString())) {
                    unicWords.remove(sb.toString());
                    notUnicWords.add(sb.toString());
                } else {
                    unicWords.add(sb.toString());
                }
            }
        }

        return unicWords;
    }
}
