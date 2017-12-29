package pro.kretov.reader;

import pro.kretov.wrapper.Wrapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadersFactory implements Readers {

    private boolean startsWith(String source, String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            return matcher.start() == 0;
        } else {
            return false;
        }
    }

    @Override
    public Wrapper<BufferedReader> getReader(String resPath) throws IOException {
        if (startsWith(resPath, "[A-Za-z]:")) {
            return new Wrapper<>(
                    new BufferedReader(new FileReader(resPath)),
                    true,
                    String.format("Resource '%s' open success.", resPath)
            );
        } else if (startsWith(resPath, "(H|h)(T|t)(T|t)(P|p)")) {
            URL url = new URL(resPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setReadTimeout(10000);
            connection.connect();
            return new Wrapper<>(
                    new BufferedReader(new InputStreamReader(connection.getInputStream())),
                    true,
                    String.format("Resource '%s' open success.", resPath)
            );
        } else {
            return new Wrapper<>(
                   null,
                    false,
                    String.format("Illegal resource path '%s'.", resPath)
            );
        }
    }

}
