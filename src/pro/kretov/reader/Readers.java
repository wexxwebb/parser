package pro.kretov.reader;

import pro.kretov.wrapper.Wrapper;

import java.io.BufferedReader;
import java.io.IOException;

public interface Readers {

    Wrapper<BufferedReader> getReader(String resPath) throws IOException;

}
