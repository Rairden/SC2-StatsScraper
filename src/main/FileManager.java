package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    void save(String file, String score) throws IOException {
        File f = new File(file);
        FileWriter writer = new FileWriter(f);
        String intToString = String.valueOf(score);
        writer.write(intToString);
        writer.close();
    }
}
