package pro.kretov;

import pro.kretov.parser.Result;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class Merger implements Runnable {

    private ConcurrentHashMap<String, Integer> uniqueWords;
    private ConcurrentSkipListSet<String> notUniqueWords;
    private Result result;
    private AtomicBoolean play;

    public Merger(
            ConcurrentHashMap<String, Integer> uniqueWords,
            ConcurrentSkipListSet<String> notUniqueWords,
            Result result,
            AtomicBoolean play) {
        this.uniqueWords = uniqueWords;
        this.notUniqueWords = notUniqueWords;
        this.result = result;
        this.play = play;
    }

    @Override
    public void run() {
        notUniqueWords.addAll(result.getNonUnique());
        for (Map.Entry<String, Integer> entry : result.getUnique().entrySet()) {
            synchronized (uniqueWords) {
                if (!play.get()) {
                    return;
                }
                if (notUniqueWords.contains(entry.getKey())) continue;
                Integer count = uniqueWords.get(entry.getKey());
                if (count != null && (count + entry.getValue()) > 3) {
                    uniqueWords.remove(entry.getKey());
                    notUniqueWords.add(entry.getKey());
                } else {
                    if (count == null) {
                        uniqueWords.put(entry.getKey(), entry.getValue());
                    } else {
                        uniqueWords.put(entry.getKey(), count + entry.getValue());
                    }
                }
            }
        }
    }
}
