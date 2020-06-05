package main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.SC2Stats.*;

public class Settings {

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
        InputStream is = getClass().getResourceAsStream("resources/settings.cfg");
        BufferedReader cfgTemplate = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        userCfg = new File(System.getProperty("user.dir") + File.separator + "settings.cfg");   // creates settings.cfg in jar execution dir

        paths = new HashMap<>();
        loadCfg(cfgTemplate, userCfg);

        DIR_SCORES = initializePath("scores");
        DIR_REPLAYS = initializePath("replays");
        NA_URL = initializePath("na");
        EU_URL = initializePath("eu");
        ALL_URL = initializePath("all");
        TEST1_URL = initializePath("test1");
        TEST2_URL = initializePath("test2");
        TEST3_URL = initializePath("test3");

        overRideTime("polldir", POLL_DIR_INTERVAL);
        overRideTime("pending", PENDING_SLEEP_TIME);
        overRideTime("notpending", NOT_PENDING_SLEEP_TIME);
    }

    private static String initializePath(String str) {
        if (paths.get(str) != null) {
            return paths.get(str);
        }
        return "";
    }

    public static Long overRideTime(String cfg, Long sleepTime) {
        if (!initializePath(cfg).isEmpty()) {
            return Long.parseLong(initializePath(cfg));
        }
        return sleepTime;
    }

    private void loadCfg(BufferedReader br, File dest) throws IOException {
        Scanner scan;
        if (dest.exists()) {
            System.out.println(dest.toString() + " (settings.cfg) exists at location dest.path().toString() " + dest.toPath().toString());
            scan = new Scanner(dest);
        } else {
            System.out.println("Could not find settings.cfg");
            copyFile(br);
            System.out.println("Now set up your settings.cfg file. Then restart the program.\n");
            System.exit(0);
            return;
        }

        while (scan.hasNextLine()) {
            String line = scan.nextLine();

            if (isComment(line) || isSection(line) || line.isEmpty()) {
                continue;
            }

            String[] keyVal = line.split("=");
            if (keyVal.length == 1) {
                continue;
            } else {
                paths.put(keyVal[0], keyVal[1]);
            }
        }
    }

    private void copyFile(BufferedReader br) throws IOException {
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
