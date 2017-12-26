package pro.kretov.writer;

import java.io.BufferedWriter;
import java.io.IOException;

public class WriteToFile implements WriteResult {

    BufferedWriter bufferedWriter;

    public WriteToFile(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    @Override
    public void writeResult(String string) throws IOException {
        bufferedWriter.write(string);
    }
}
