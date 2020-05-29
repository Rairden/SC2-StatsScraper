package main;

import java.io.*;
import java.util.Arrays;

import static main.Matchup.*;
import static main.WinRate.winRate;

public class FileManager {

    File file;
    int numFiles;
    String replayDirLinux = "/home/erik/scratch/SC2-scraper/replays/";
    String replayDirWin10 = "E:\\SC2\\replayBackup\\";

    public FileManager() {
        file = new File(replayDirWin10);
        numFiles = file.list().length;
    }

    void save(String fullPath, int[] score) throws IOException {
        File file = new File(fullPath);
        if (winRate.matchup != RESET && !isModified(fullPath, file, score)) {
            return;
        }
        writeFile(score, file);
    }

    boolean isModified(String path, File file, int[] score) throws IOException {
        if (file.length() > 0) {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String str;
            while ((str = br.readLine()) != null) {
                String[] strArr = str.split("\\s");
                int[] arr = {Integer.parseInt(strArr[0]), Integer.parseInt(strArr[2])};
                if (Arrays.hashCode(arr) == Arrays.hashCode(score)) {
                    // System.err.println("File hasn't changed");
                    return false;
                }
            }
        }
        return true;
    }

    File getLastModified() {
        File[] files = this.file.listFiles(File::isFile);
        long lastModifiedTime = Long.MIN_VALUE;

        File chosenFile = files[0];
        for (File f : files) {
            if (f.lastModified() > lastModifiedTime) {
                chosenFile = f;
                lastModifiedTime = f.lastModified();
            }
        }
        return chosenFile;
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

    int numberOfFiles() {
        return file.list().length;
    }
}
