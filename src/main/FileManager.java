package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    public void save(String file, String text) throws IOException {
        File f = new File(file);
        FileWriter writer = new FileWriter(f);
        writer.write(text);
        writer.close();
    }

    public void save(String file, int wins) throws IOException {
        File f = new File(file);
        FileWriter writer = new FileWriter(f);
        String intToString = String.valueOf(wins);
        writer.write(intToString);
        writer.close();
    }
}
