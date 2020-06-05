package main;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.Matchup.*;
import static main.Settings.*;
import static main.WinRate.*;

/**
 * @author Erik Rairden - 6/4/2020
 */
// https://www.youtube.com/watch?v=0s8O7jfy3c0
// https://stackoverflow.com/questions/17315886/extract-and-group-elements-together-with-jsoup

public class SC2Stats extends TimerTask {

    FileManager fileMgr;
    static String url;

    boolean firstLoop = true;
    boolean hasPlayedPast24Hrs = false;
    static boolean updatedServer = false;
    static boolean processingRep = false;
    static boolean resetAllGames = false;

    static long POLL_DIR_INTERVAL  = 2000;
    static long PENDING_SLEEP_TIME = 4000;
    static long NOT_PENDING_SLEEP_TIME = 6000;

    public SC2Stats() {
        fileMgr = new FileManager();
    }

    public static void main(String[] args) throws IOException {
        WinRate.getInstance();
        new Settings();
        Scanner scan = new Scanner(System.in);
        Timer timer = new Timer();
        SC2Stats timerTask = new SC2Stats();

        determineServer(args);
        System.out.println("Attempting to download web page from: \n" + url + "\n");

        timerTask.buildFilePath(DIR_SCORES);
        timer.schedule(timerTask, 10000, POLL_DIR_INTERVAL);   // 1000 = 1 second

        while (true) {
            if (scan.hasNextLine()) {
                String[] in = scan.nextLine().toUpperCase().split("\\s");

                if (in[0].equals("RESET")) {
                    winRate.score_ZvP_reset = WinRate.getInstance().score_ZvP.clone();
                    winRate.score_ZvT_reset = WinRate.getInstance().score_ZvT.clone();
                    winRate.score_ZvZ_reset = WinRate.getInstance().score_ZvZ.clone();
                    resetAllGames = true;
                    System.out.println("\nResetting all games to zero.\n");
                    continue;
                }

                determineServer(in);
                updatedServer = true;
                resetAllGames = false;
                System.out.println("\n Switched to server: " + in[0] + "\nWeb page located at: " + url + "\n");
            }
        }
    }

    private static void determineServer(String[] args) {
        if (args.length == 0) {
            url = TEST2_URL;
            return;
        }
        switch (args[0].toLowerCase()) {
            case "na"    -> url = NA_URL;
            case "eu"    -> url = EU_URL;
            case "all"   -> url = ALL_URL;
            case "test1" -> url = TEST1_URL;
            case "test2" -> url = TEST2_URL;
            case "test3" -> url = TEST3_URL;
            default      -> url = TEST2_URL;
        }
    }

    @Override
    public void run() {
        if (processingRep) {

            // if sc2replaystats.com hasn't parsed your replay, try 3 times. After that, ignore/move on.
            for (int i = 0; i < 3; i++) {
                webScrape(PENDING_SLEEP_TIME);

                if (!processingRep) {
                    PENDING_SLEEP_TIME = overRideURI("pending", 30000L);
                    break;
                }
            }
            processingRep = false;

        } else {
            if (fileMgr.numberOfFiles() == fileMgr.numFiles) {
                return;
            }

            fileMgr.numFiles = fileMgr.numberOfFiles();

            // Skip if replay is vs A.I.: "2020-05-26 [ZvT] A.I. 1 (Elite), Rairden - Zen LE.SC2Replay"
            File lastModified = fileMgr.getLastModified();

            String fileName = lastModified.getName();
            String regex = "A\\.I\\..*\\.SC2Replay$";       //    A\.I\..*\.SC2Replay$
            Pattern regexp = Pattern.compile(regex);
            Matcher match = regexp.matcher(fileName);

            if (match.find()) { return; }

            webScrape(NOT_PENDING_SLEEP_TIME);
        }
    }

    void webScrape(long millis) {
        try {
            Thread.sleep(millis);
            Connection.Response response = Jsoup.connect(url).userAgent("Chrome/81.0").execute();

            if (response.statusCode() == 200) {
                Document doc = Jsoup.connect(url).userAgent("Chrome/81.0").get();

                if (!hasBeenParsed(doc)) return;

                Elements headings = doc.select("h2");
                if (firstLoop) {
                    System.out.println("Download successful.\n");
                    System.out.println(" Poll directory interval: " + POLL_DIR_INTERVAL / 1000 + " seconds");
                    System.out.println("Delay before web parsing: " + NOT_PENDING_SLEEP_TIME / 1000 + " seconds\n");
                    firstLoop = false;
                }

                // if no games in past 24 hours, then set all win rates to 0.
                if (!hasPlayedPast24Hrs || updatedServer) {
                    for (Element e : headings) {
                        if (e.toString().equals("<h2>24 Hours <strong>Quick</strong> Statistics</h2>")) {
                            String str = e.nextElementSibling().selectFirst("section").text();

                            if (str.equals("No games have been played")) {
                                WinRate.getInstance().update("null", "null");
                                buildFilePath(DIR_SCORES);
                                return;
                            } else {
                                break;
                            }
                        }
                    }
                }

                for (Element e : headings) {
                    if (e.toString().equals("<h2>24 Hours <strong>Race </strong> Statistics</h2>")) {
                        hasPlayedPast24Hrs = true;
                        Elements winrate = e.nextElementSibling().select("div.col-md-2");

                        for (Element x : winrate) {
                            String matchup = x.getElementsByTag("label").first().text();
                            String score = x.getElementsByTag("strong").first().text();

                            WinRate.getInstance().update(matchup, score, resetAllGames);
                            buildFilePath(DIR_SCORES);
                        }

                        if (winrate.size() < 3) {
                            winRate.matchup = RESET;
                            List<String> matchupFound = new ArrayList<>();
                            for (Element i : winrate) {
                                matchupFound.add(i.getElementsByTag("label").first().text());
                            }

                            List<String> missingMatchup = winRate.determineMissingMatchup(matchupFound);
                            for (String s : missingMatchup) {
                                buildFilePath(DIR_SCORES, s);
                            }
                        }
                        return;
                    }
                }
            } else {
                System.out.println("Cannot connect to that web page.");
                System.exit(0);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Cannot connect to that web page or the file path does not exist.");
            System.exit(0);
        }
    }

    // Sometimes their server takes longer than 60 seconds to parse replays.
    private boolean hasBeenParsed(Document doc) {
        Element alert = doc.getElementsByClass("alert alert-info").first();

        if (alert == null) {
            processingRep = false;
            PENDING_SLEEP_TIME = Long.parseLong(paths.get("pending"));
            return true;
        }

        if (alert.text().contains("They will be processed shortly")) {
            fileMgr.numFiles = fileMgr.numberOfFiles() + 1;
            PENDING_SLEEP_TIME *= 3;
            processingRep = true;
            return false;
        }
        return true;
    }

    private void buildFilePath(String dir, String... exclude) throws IOException {
        switch (winRate.matchup) {
            case ZvP -> saveFile(dir, "ZvP.txt", winRate.score_ZvP);
            case ZvT -> saveFile(dir, "ZvT.txt", winRate.score_ZvT);
            case ZvZ -> saveFile(dir, "ZvZ.txt", winRate.score_ZvZ);
            case NULL -> {
                saveFile(dir, "ZvP.txt", winRate.score_reset);
                saveFile(dir, "ZvT.txt", winRate.score_reset);
                saveFile(dir, "ZvZ.txt", winRate.score_reset);
            }
            case RESET -> saveFile(dir, exclude[0] + ".txt", winRate.score_reset);
            default -> throw new IllegalArgumentException("Argument must be one of the three: ZvP, ZvT, ZvZ");
        }
    }

    private void saveFile(String dir, String fileName, int[] score) throws IOException {
        fileMgr.save(dir + fileName, score);
    }
}
