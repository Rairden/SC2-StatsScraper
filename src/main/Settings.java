package main;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.FileManager.*;

public class Settings {

    File cfgTemplate;
    File userCfg;

    static Map<String, String> paths;
    static String DIR_SCORES;
    static String DIR_REPLAYS;
    static String NA_URL;
    static String EU_URL;
    static String ALL_URL;
    static String TEST1_URL;
    static String TEST2_URL;
    static String TEST3_URL;

    public Settings() throws IOException {
        // String cfgTemplate = new File(SC2Stats.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath() + template;
        cfgTemplate = new File("lib/settings.cfg");
        userCfg = new File(System.getProperty("user.dir") + File.separator + "settings.cfg");   // creates settings.cfg in jar execution dir

        paths = new HashMap<>();
        loadCfg();
        DIR_SCORES = initializeURI("scores");
        DIR_REPLAYS = initializeURI("replays");
        NA_URL = initializeURI("na");
        EU_URL = initializeURI("eu");
        ALL_URL = initializeURI("all");
        TEST1_URL = initializeURI("test1");
        TEST2_URL = initializeURI("test2");
        TEST3_URL = initializeURI("test3");
        // writeFile();
    }

    private static String initializeURI(String str) {
        if (paths.get(str) != null) {
            return paths.get(str);
        }
        return "";
    }

    private void loadCfg() throws IOException {
        Scanner scan;
        if (userCfg.exists()) {
            scan = new Scanner(userCfg);
        } else {
            copyFile(cfgTemplate, userCfg);
            System.out.println("Now set up your settings.cfg file. Then restart the program.");
            System.exit(0);
            return;
        }

        while (scan.hasNextLine()) {
            String line = scan.nextLine();

            if (isComment(line) || isSection(line) || line.isEmpty()) {
                continue;
            }

            String[] keyVal = line.split("=");
            paths.put(keyVal[0], keyVal[1]);
        }
    }

    private void copyFile(File src, File dest) throws IOException {
        if (!userCfg.exists()) {
            Files.copy(src.toPath(), dest.toPath());
        }
    }

    // same result as java.nio.CopyFile(File, File)
    private void copyFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(cfgTemplate));

        String str = "";
        StringBuilder sb = new StringBuilder();
        while ((str = br.readLine()) != null) {
            sb.append(str).append("\n");
        }

        FileWriter fw = new FileWriter(userCfg);
        fw.write(sb.toString());
        fw.close();
    }

    private boolean isComment(String line) {
        String regexComment = "^#.*";
        Pattern p = Pattern.compile(regexComment);
        Matcher m = p.matcher(line);

        return m.matches();
    }

    private boolean isSection(String line) {
        String regexBrackets = "^\\[.*\\]$";
        Pattern p = Pattern.compile(regexBrackets);
        Matcher m = p.matcher(line);

        return m.matches();
    }
}
