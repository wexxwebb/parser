package pro.kretov.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
public class Parser implements Callable<Result> {

    private BufferedReader bufReader;
    private String regExp;
    private Map<String, Integer> uniqueWord;
    private AtomicBoolean play;
    private String resourcePath;

    public Parser(
            BufferedReader bufReader,
            String regExp,
            AtomicBoolean play,
            String resourcePath) {

        this.bufReader = bufReader;
        this.regExp = regExp;
        this.play = play;
        this.resourcePath = resourcePath;
        this.uniqueWord = new HashMap<>();
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
            while ((string = bufReader.readLine()) != null) {
                if (!checkString(string)) {
                    play.set(false);
                    System.out.println("Program closed. Illegal symbols found.");
                    bufReader.close();
                    return null;
                }
                matcher = pattern.matcher(string);
                while (matcher.find()) {
                    if (!play.get()) {
                        bufReader.close();
                        return null;
                    }
                    String word = string.substring(matcher.start(), matcher.end());
                    Integer count = uniqueWord.get(word);
                    if (count == null) {
                        uniqueWord.put(word, 1);
                    } else {
                        uniqueWord.put(word, count + 1);
                    }
                }
            }
            bufReader.close();
        } catch (IOException e) {
            System.out.printf("Can't read resource '%s'. Skipped.", resourcePath);
            return null;
        }
        return new Result(uniqueWord);
    }
}
