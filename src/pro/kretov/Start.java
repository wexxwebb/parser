package pro.kretov;

import pro.kretov.founder.resFounder.Founder;
import pro.kretov.founder.resFounder.ResListParser;
import pro.kretov.founder.reader.Readers;
import pro.kretov.founder.reader.ReadersFactory;
import pro.kretov.parser.Parser;
import pro.kretov.parser.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Start {

    private static boolean sleep(long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e1) {
            return false;
        }
    }

    public static void main(String[] args) {
        String fileResListName = "resources.txt";
        Founder resFounder;
        int retry = 0;
        while (true) {
            try {
                resFounder = new ResListParser(fileResListName);
                break;
            } catch (IOException e) {
                retry++;
                if (retry > 5) {
                    System.out.printf("Can't read file %s. Exit.", fileResListName);
                    return;
                } else {
                    if (retry == 1) {
                        System.out.printf("Reading file %s.", fileResListName);
                    }
                    sleep(200);
                    System.out.print(".");
                }
            }
        }

        AtomicBoolean play = new AtomicBoolean(true);
        ConcurrentHashMap<String, Integer> uniqueWords = new ConcurrentHashMap<>();
        ConcurrentSkipListSet<String> notUniqueWords = new ConcurrentSkipListSet<>();
        ExecutorService exec = Executors.newCachedThreadPool();
        List<Future<Result>> futures = new ArrayList<>();

        Readers readers = new ReadersFactory();
        for (String resourcePath : resFounder.getResourceList()) {
            if (!play.get()) break;
            BufferedReader bufr;
            retry = 0;
            while (play.get()) {
                try {
                    bufr = readers.getReader(resourcePath);
                    if (bufr == null) {
                        System.out.printf("Illegal resource path '%s'. Skipped.\n", resourcePath);
                        break;
                    }
                    futures.add(exec.submit(new Parser(bufr, "[а-яА-Я]+", play, resourcePath)));
                    break;
                } catch (IOException e) {
                    retry++;
                    if (retry > 5) {
                        System.out.printf("Can't read resource. Skipped.\n");
                        break;
                    } else {
                        if (retry == 1) {
                            System.out.printf("Reading resource '%s'.", resourcePath);
                        }
                        sleep(200);
                        System.out.print(".");
                    }
                }
            }
        }
        ArrayList<Thread> mergers = new ArrayList<>();
        while (true) {
            Iterator<Future<Result>> futureIterator = futures.iterator();
            while (play.get() && futureIterator.hasNext()) {
                Future<Result> temp = futureIterator.next();
                if (temp != null && temp.isDone()) {
                    try {
                        Thread thread = new Thread(new Merger(uniqueWords, notUniqueWords, temp.get(), play));
                        thread.start();
                        mergers.add(thread);
                        futureIterator.remove();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } else if (temp == null && temp.isDone()) {
                    futureIterator.remove();
                }
            }
            if (futures.size() == 0 || !play.get()) {
                exec.shutdown();
                break;
            }
        }
        boolean stop = false;
        while (!stop && play.get()) {
            stop = true;
            for (Thread thread : mergers) {
                if (thread.isAlive()) {
                    stop = false;
                }
            }
        }
        for (Map.Entry<String, Integer> entry : uniqueWords.entrySet()) {
            if (!play.get()) return;
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.out.println("Total: " + uniqueWords.size());

    }
}
