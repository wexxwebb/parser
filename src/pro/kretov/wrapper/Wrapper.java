package pro.kretov.wrapper;

public class Wrapper<T> {

    private T content;
    private boolean success;
    private String info;

    public Wrapper(T content, boolean success, String info) {
        this.content = content;
        this.success = success;
        this.info = info;
    }

    public T getContent() {
        return content;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getInfo() {
        return info;
    }
}
