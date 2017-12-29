package pro.kretov.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
public class UniqueWordsParser implements Callable<Result>, Parser {

    private BufferedReader reader;
    private String regExp;
    private Map<String, Integer> uniqueWords;
    private AtomicBoolean play;
    private String resourcePath;

    public UniqueWordsParser(
            BufferedReader reader,
            String regExp,
            AtomicBoolean play,
            String resourcePath) {

        this.reader = reader;
        this.regExp = regExp;
        this.play = play;
        this.resourcePath = resourcePath;
        this.uniqueWords = new HashMap<>();
    }

    private boolean checkString(String string) {
        return !string.matches("[A-Za-z]+");
    }

    @Override
    public Result call() {
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher;
        String string;
        try {
            while ((string = reader.readLine()) != null) {
                if (!checkString(string)) {
                    play.set(false);
                    System.out.println("Program closed. Illegal symbols found.");
                    reader.close();
                    return null;
                }
                matcher = pattern.matcher(string);
                while (matcher.find()) {
                    if (!play.get()) {
                        reader.close();
                        return null;
                    }
                    String word = string.substring(matcher.start(), matcher.end());
                    Integer count = uniqueWords.get(word);
                    if (count == null) {
                        uniqueWords.put(word, 1);
                    } else {
                        uniqueWords.put(word, count + 1);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.printf("Can't read resource '%s'. Skipped.", resourcePath);
            return null;
        }
        System.out.printf("In file '%s' found %d unique words.\n", resourcePath, uniqueWords.size());
        return new Result(uniqueWords);
    }
}
