package pro.kretov.threads;

import pro.kretov.parser.Result;
import pro.kretov.wrapper.Wrapper;

import java.util.concurrent.Callable;

public interface Threads {

    Wrapper<Callable<Result>> build(String filePath);

}
