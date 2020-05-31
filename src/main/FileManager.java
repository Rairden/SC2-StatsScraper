package main;

import java.io.*;
import java.util.Arrays;

import static main.Matchup.*;
import static main.WinRate.winRate;

public class FileManager {

    File file;
    int numFiles;
    static final String REPLAY_DIR_LINUX = "/home/erik/scratch/SC2-scraper/replays/";
    static final String REPLAY_DIR_WIN10 = "E:\\SC2\\replayBackup\\";

    public FileManager() {
        file = new File(REPLAY_DIR_LINUX);
        numFiles = file.list().length;
    }

    public void save(String fullPath, int[] score) throws IOException {
        File file = new File(fullPath);
        if (winRate.matchup != RESET && !isModified(fullPath, file, score)) {
            return;
        }
        writeFile(score, file);
    }

    /**
     * There's no need to write to disk if the data has not changed.
     *
     * @return false if the existing file matches the data that has been parsed.
     * @throws IOException If an I/O error occurs
     */
    private boolean isModified(String fullPath, File file, int[] score) throws IOException {
        if (file.length() > 0) {
            BufferedReader br = new BufferedReader(new FileReader(fullPath));
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

    // I only need to regex match the most recent file, not all 1000 files.
    public File getLastModified() {
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

    private void writeFile(int[] score, File f) throws IOException {
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

    public int numberOfFiles() {
        return file.list().length;
    }
}
