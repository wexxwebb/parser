package pro.kretov.parser;

import pro.kretov.parser.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
public class Parser implements Callable<Result> {

    private BufferedReader bufReader;
    private String regExp;
    private Map<String, Integer> uniqueWord;
    private Set<String> notUniqueWords;
    private AtomicBoolean play;
    private String resourcePath;
    private Pattern pattern;
    private Matcher matcher;

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
        this.notUniqueWords = new TreeSet<>();
    }

    private boolean checkString(String string) {
        Pattern patter = Pattern.compile("[A-Za-z]+");
        Matcher matcher = patter.matcher(string);
        if (matcher.find()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Result call() {
        pattern = Pattern.compile(regExp);
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
                    if (!notUniqueWords.contains(word)) {
                        Integer count = uniqueWord.get(word);
                        if (count != null && count >= 3) {
                            uniqueWord.remove(word);
                            notUniqueWords.add(word);
                        } else {
                            if (count != null) {
                                uniqueWord.put(word, count + 1);
                            } else {
                                uniqueWord.put(word, 1);
                            }
                        }
                    }
                }
            }
            bufReader.close();
        } catch (IOException e) {
            System.out.printf("Can't read resource '%s'. Skipped.", resourcePath);
            return null;
        }
        return new Result(uniqueWord, notUniqueWords);
    }
}
