package pro.kretov;

import pro.kretov.parser.Result;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Merger implements Runnable {

    private final ConcurrentHashMap<String, Integer> uniqueWords;
    private Result result;
    private AtomicBoolean play;

    public Merger(
            ConcurrentHashMap<String, Integer> uniqueWords,
            Result result,
            AtomicBoolean play) {
        this.uniqueWords = uniqueWords;
        this.result = result;
        this.play = play;
    }

    @Override
    public void run() {
        for (Map.Entry<String, Integer> entry : result.getUnique().entrySet()) {
            synchronized (uniqueWords) {
                if (!play.get()) {
                    return;
                }
                Integer count = uniqueWords.get(entry.getKey());
                if (count == null) {
                    uniqueWords.put(entry.getKey(), entry.getValue());
                } else {
                    uniqueWords.put(entry.getKey(), count + entry.getValue());
                }
            }
        }
    }
}
