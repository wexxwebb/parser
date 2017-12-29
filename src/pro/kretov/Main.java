package pro.kretov;

import pro.kretov.resoucer.founder.ResListParser;
import pro.kretov.reader.ReadersFactory;
import pro.kretov.parser.Result;
import pro.kretov.resoucer.builder.ResourceListFactory;
import pro.kretov.threads.Threads;
import pro.kretov.threads.ThreadsFactory;
import pro.kretov.wrapper.Wrapper;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static void main(String[] args) {

        final String fileResListName = "resources.txt";

        final ResourceListFactory resListBuilder = new ResourceListFactory(new ResListParser(fileResListName));
        final Wrapper<List<String>> resourceListWrap = resListBuilder.build();

        if (!resourceListWrap.isSuccess()) {
            System.out.println(resourceListWrap.getInfo());
            return;
        }

        final AtomicBoolean play = new AtomicBoolean(true);
        final ExecutorService exec = Executors.newCachedThreadPool();
        final List<Future<Result>> futures = new ArrayList<>();

        final Threads threadsFactory = new ThreadsFactory(new ReadersFactory(), "[а-яА-ЯЁё]+", play);

        for (String resourcePath : resourceListWrap.getContent()) {
            Wrapper<Callable<Result>> callableWrap = threadsFactory.build(resourcePath);
            if (!callableWrap.isSuccess()) {
                System.out.println(callableWrap.getInfo() + " Skipped.");
            } else {
                System.out.printf("Reading resource '%s'.\n", resourcePath);
                futures.add(exec.submit(callableWrap.getContent()));
            }
        }

        final ConcurrentHashMap<String, Integer> words = new ConcurrentHashMap<>();
        final ArrayList<Thread> mergers = new ArrayList<>();

        while (true) {
            Iterator<Future<Result>> futureIterator = futures.iterator();
            while (play.get() && futureIterator.hasNext()) {
                Future<Result> temp = futureIterator.next();
                if (temp != null && temp.isDone()) {
                    try {
                        Thread thread = new Thread(new Merger(words, temp.get(), play));
                        thread.start();
                        mergers.add(thread);
                        futureIterator.remove();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        futureIterator.remove();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        futureIterator.remove();
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

        final ArrayList<Map.Entry<String, Integer>> unique = new ArrayList<>();

        if (play.get()) {
            words.entrySet().stream().filter(
                    (entry) -> entry.getValue() <= 3
            ).forEach(
                    (entry) -> {
                        unique.add(entry);
                        System.out.println(entry.getValue() + " " + entry.getKey());
                    }
            );
            System.out.println("Total: " + unique.size());
        }
    }
}
