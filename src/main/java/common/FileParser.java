package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class FileParser {

    public static List<String> inputToStrList(String filename) throws IOException {
        InputStream is = FileParser.class.getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        // Read input
        String line;
        List<String> lines = new LinkedList<String>();

        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }
}
