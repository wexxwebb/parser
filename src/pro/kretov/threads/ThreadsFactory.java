package pro.kretov.threads;

import pro.kretov.parser.Result;
import pro.kretov.parser.UniqueWordsParser;
import pro.kretov.reader.Readers;
import pro.kretov.sleeper.Sleeper;
import pro.kretov.wrapper.Wrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadsFactory implements Threads {

    private Readers readers;
    private String regexp;
    private AtomicBoolean play;

    public ThreadsFactory(Readers readers, String regexp, AtomicBoolean play) {
        this.readers = readers;
        this.regexp = regexp;
        this.play = play;
    }

    private Wrapper<Callable<Result>> create(String filePath, int retry) {
        try {
            Wrapper<BufferedReader> reader = readers.getReader(filePath);
            if (!reader.isSuccess()) return new Wrapper<>(null, false, reader.getInfo());
            else return new Wrapper<>(
                    new UniqueWordsParser(reader.getContent(), regexp, play, filePath),
                    true,
                    "Resource open success");
        } catch (IOException e) {
            retry++;
            if (retry > 5) return new Wrapper<>(null, false, "Can't open resource");
            else if (retry == 1) System.out.printf("Opening recource '%s'.", filePath);
            else System.out.print(".");
            Sleeper.sleep(500);
            return create(filePath, retry);
        }
    }

    @Override
    public Wrapper<Callable<Result>> build(String filePath) {
        Wrapper<Callable<Result>> thread = create(filePath, 0);
        if (!thread.isSuccess()) {
            return new Wrapper<>(null, false, "Can't open resource. " + thread.getInfo());
        } else {
            return new Wrapper<>(thread.getContent(), true, String.format("Resource '%s' opening sucecess", filePath));
        }
    }
}
