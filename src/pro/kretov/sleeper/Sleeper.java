package pro.kretov.sleeper;

public class Sleeper {

    static public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            return;
        }
    }

}
