package pro.kretov.founder.reader;

import java.io.BufferedReader;
import java.io.IOException;

public interface Readers {

    BufferedReader getReader(String resPath) throws IOException;

}
