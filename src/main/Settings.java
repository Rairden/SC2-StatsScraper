package main;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static main.SC2Stats.*;

public class Settings {

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
        InputStream cfgTemplate = getClass().getResourceAsStream("resources/settings.cfg");
        File userCfg = new File(System.getProperty("user.dir") + File.separator + "settings.cfg");   // creates settings.cfg in jar execution dir

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

        POLL_DIR_INTERVAL = overRideTime("polldir", POLL_DIR_INTERVAL);
        PENDING_SLEEP_TIME = overRideTime("pending", PENDING_SLEEP_TIME);
        NOT_PENDING_SLEEP_TIME = overRideTime("notpending", NOT_PENDING_SLEEP_TIME);
    }

    private static String initializePath(String str) {
        if (paths.get(str) != null) {
            return paths.get(str);
        }
        return "";
    }

    public static Long overRideTime(String cfg, Long defaultSleepTime) {
        if (!initializePath(cfg).isEmpty()) {
            return Long.parseLong(initializePath(cfg));
        }
        return defaultSleepTime;
    }

    private void loadCfg(InputStream cfgTemplate, File userCfg) throws IOException {
        Scanner scan;
        if (userCfg.exists()) {
            scan = new Scanner(userCfg);
        } else {
            copyFile(cfgTemplate, userCfg);
            System.out.println("Now set up your settings.cfg file. Then restart the program.\n");
            System.exit(0);
            return;
        }

        while (scan.hasNextLine()) {
            String line = scan.nextLine();

            // skip comments '#', sections '[]' or empty lines
            if (line.matches("^#.*|\\[.*\\].*|\\s*")) {
                continue;
            }

            String[] keyVal = line.trim().split("=");
            if (keyVal.length == 2) {
                paths.put(keyVal[0], keyVal[1]);
            }
        }
    }

    private void copyFile(InputStream src, File dest) throws IOException {
        Files.copy(src, dest.toPath());
    }
}
