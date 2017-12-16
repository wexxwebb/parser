package pro.kretov;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Start {

    public static void main(String[] args) {

        Founder fileFounder = new FileFounder("../BookReader/txt", "txt");
        ExecutorService execService = Executors.newFixedThreadPool(1);
        List<String> fileList = fileFounder.getResList();
        Iterator<String> it = fileList.iterator();
        List<Future<Set<String>>> futures = new ArrayList<>();
        while (it.hasNext()) {
            String fileName = it.next();
            futures.add(execService.submit(new UnicWordFileParser(fileName, "([A-Z]|[a-z]|[а-я]|[А-Я])+([a-z]*|[а-я]*)")));
        }

        while (true) {
            boolean stop = false;
            Iterator<Future<Set<String>>> futureIt = futures.iterator();
            while (futureIt.hasNext()) {
                stop = futureIt.next().isDone();
            }
            if (stop) {
                execService.shutdown();
                break;
            }
        }
    }
}
