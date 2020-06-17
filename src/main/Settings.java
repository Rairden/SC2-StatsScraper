package main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

    static long POLL_DIR_INTERVAL  = 5000;
    static long PENDING_SLEEP_TIME = 30000;
    static long NOT_PENDING_SLEEP_TIME = 75000;

    public Settings() throws IOException {
        InputStream is = getClass().getResourceAsStream("resources/settings.cfg");
        BufferedReader cfgTemplate = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        File userCfg = new File(System.getProperty("user.dir") + File.separator + "settings.cfg");   // creates settings.cfg in jar execution dir

        paths = new HashMap<>();
        loadCfg(cfgTemplate, userCfg);

        DIR_SCORES = setPath("scores");
        DIR_REPLAYS = setPath("replays");
        NA_URL = setPath("na");
        EU_URL = setPath("eu");
        ALL_URL = setPath("all");
        TEST1_URL = setPath("test1");
        TEST2_URL = setPath("test2");
        TEST3_URL = setPath("test3");

        POLL_DIR_INTERVAL = Long.parseLong(paths.get("polldir"));
        PENDING_SLEEP_TIME = Long.parseLong(paths.get("pending"));
        NOT_PENDING_SLEEP_TIME = Long.parseLong(paths.get("notpending"));
    }

    private String setPath(String type) {
        if (paths.get(type) == null && type.equals("scores")) {
            return System.getProperty("user.dir") + File.separator;
        }
        return paths.get(type);
    }

    private void loadCfg(BufferedReader cfgTemplate, File userCfg) throws IOException {
        Scanner scan;
        if (userCfg.exists()) {
            scan = new Scanner(userCfg);
        } else {
            copyFile(cfgTemplate, userCfg);
            System.out.println("Now set up your settings.cfg file. Then restart the program.\n");
            System.exit(0);
            return;
        }

        paths.put("polldir", String.valueOf(POLL_DIR_INTERVAL));
        paths.put("pending", String.valueOf(PENDING_SLEEP_TIME));
        paths.put("notpending", String.valueOf(NOT_PENDING_SLEEP_TIME));

        boolean isNumber = false;

        while (scan.hasNextLine()) {
            String line = scan.nextLine();

            // skip comments '#', sections [] or empty lines
            if (line.matches("^#.*|\\[(scores|replays|url)]|\\s*")) continue;

            if (line.matches("^\\[time]")) {
                isNumber = true;
                continue;
            }
            overRideSettings(line, isNumber);
        }

        if (paths.get("replays") == null || paths.get("all") == null) {
            throw new IllegalStateException("The minimum configuration is to populate: replays= and all=");
        }
    }

    private void overRideSettings(String line, boolean isNumber) {
        String[] keyVal = line.trim().split("=");

        if (!(keyVal.length == 2)) return;

        if (isNumber) {
            try {
                if (Long.parseLong(keyVal[1]) <= 0) {
                    throw new IllegalArgumentException(keyVal[0] + " must be a number > 0.");
                }
                paths.put(keyVal[0], keyVal[1]);
            } catch (NumberFormatException e) {
                throw new NumberFormatException(keyVal[0] + " must be a number.");
            }
            return;
        }

        paths.put(keyVal[0], keyVal[1]);
    }

    private void copyFile(BufferedReader br, File userCfg) throws IOException {
        String str = "";
        StringBuilder sb = new StringBuilder();

        while ((str = br.readLine()) != null) {
            sb.append(str).append("\n");
        }
        FileWriter fw = new FileWriter(userCfg);
        fw.write(sb.toString());
        fw.close();
    }

    // can't get this to work with forward/backslash or double backslash in settings.cfg (FileNotFoundException)
    private void copyFile(InputStream src, File dest) throws IOException {
        Files.copy(src, dest.toPath());
    }
}
