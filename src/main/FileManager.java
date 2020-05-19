package main;

import java.io.*;
import java.util.Arrays;

public class FileManager {

    void checkIfFileExists(String path, int[] score) throws IOException {
        File file = new File(path);
        if (file.length() > 0) {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String str;
            while ((str = br.readLine()) != null) {
                String[] strArr = str.split("\\s");
                int[] a = {Integer.parseInt(strArr[0]), Integer.parseInt(strArr[2])};
                if (Arrays.hashCode(a) == Arrays.hashCode(score)) {
                    // System.err.println("File hasn't changed");
                    return;
                }
            }
        }
        writeFile(score, file);
    }

    void writeFile(int[] score, File f) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            if (i == 1) {
                sb.append(" - ");
            }
            sb.append(score[i]).append(" ");
            sb.setLength(sb.length() - 1);
        }
        FileWriter writer = new FileWriter(f);
        writer.write(sb.toString());
        writer.close();
    }
}
