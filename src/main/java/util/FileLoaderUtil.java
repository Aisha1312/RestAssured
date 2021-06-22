package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class FileLoaderUtil extends TestBase {

    public static String loadFile(String path) throws IOException {
        try (BufferedReader buffer = new BufferedReader
                (new InputStreamReader(FileLoaderUtil.class.getClassLoader().getResourceAsStream(path)))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        } catch (Exception exp) {
            System.out.println("The message is " + exp.getMessage());
            exp.printStackTrace();
            throw new IllegalStateException(exp);

        }
    }
}
