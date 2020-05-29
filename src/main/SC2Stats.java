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

// https://www.youtube.com/watch?v=0s8O7jfy3c0
// https://stackoverflow.com/questions/17315886/extract-and-group-elements-together-with-jsoup

public class SC2Stats extends TimerTask {

    Scanner scan;
    WinRate winrates;
    FileManager fileMgr;
    boolean firstLoop = true;
    boolean hasPlayedPast24Hrs = false;
    static boolean updatedServer = false;
    static long period = 3000;
    static int Thsleep = 5000;

    static String url;
    static String dirLinux  = "/home/erik/scratch/SC2-scraper/";
    static String dirWin10  = "C:\\Users\\Erik\\Documents\\OBS-win10-sc2\\sc2-Streaming\\winrate\\";
    static String NA_url    = "https://sc2replaystats.com/account/display/49324";
    static String EU_url    = "https://sc2replaystats.com/account/display/49324/0/2794640/1v1/AutoMM/43/";
    static String ALL_url   = "https://sc2replaystats.com/account/display/49324/0/195960-2794640/1v1/AutoMM/43/";
    static String TEST1_url  = "http://localhost/webscraper/1pending.html";
    static String TEST2_url  = "http://localhost/webscraper/2games-past24.html";
    static String TEST3_url  = "http://localhost/webscraper/3games-past24-bothAccounts.html";

    public SC2Stats() {
        winrates = WinRate.getInstance();
        fileMgr = new FileManager();
        scan = new Scanner(System.in);
    }

    public static void main(String[] args) throws IOException {
        determineServer(args);
        System.out.println("Attempting to download web page from: \n" + url + "\n");
        Timer timer = new Timer();
        SC2Stats timertask = new SC2Stats();
        timertask.buildFilePath(dirLinux);
        timer.schedule(timertask, 0, period);   // 1000 = 1 second

        while (true) {
            if (timertask.scan.hasNextLine()) {
                String[] in = timertask.scan.nextLine().toUpperCase().split("\\s");
                determineServer(in);
                updatedServer = true;
                System.out.println("\n Switched to server: " + in[0] + "\nWeb page located at: " + url + "\n");
            }
        }
    }

    static void determineServer(String[] args) {
        if (args.length == 0) {
            url = TEST1_url;
            return;
        }
        switch (args[0].toLowerCase()) {
            case "na"   -> url = NA_url;
            case "eu"   -> url = EU_url;
            case "all"  -> url = ALL_url;
            case "test1" -> url = TEST1_url;
            case "test2" -> url = TEST2_url;
            case "test3" -> url = TEST3_url;
            default     -> url = TEST1_url;
        }
    }

    @Override
    public void run() {
        if (fileMgr.numberOfFiles() == fileMgr.numFiles) {
            return;
        }

        fileMgr.numFiles = fileMgr.numberOfFiles();

        // Skip if replay is vs A.I.: "2020-05-26 [ZvT] A.I. 1 (Elite), Rairden - Zen LE.SC2Replay"
        File lastModified = fileMgr.getLastModified();

        String fileName = lastModified.getName();
        String regex = "A\\.I\\..*\\.SC2Replay$";      //    A\.I\..*\.SC2Replay$
        Pattern regexp = Pattern.compile(regex);
        Matcher match = regexp.matcher(fileName);

        if (match.find()) { return; }

        try {
            Thread.sleep(Thsleep);
        } catch (InterruptedException ignored) {}

        webScrape();
    }

    void webScrape() {
        try {
            Connection.Response response = Jsoup.connect(url).userAgent("Chrome/81.0").execute();

            if (response.statusCode() == 200) {
                Document doc = Jsoup.connect(url).userAgent("Chrome/81.0").get();
                Elements headings = doc.select("h2");

                if (firstLoop) {
                    System.out.println("Download successful.\n");
                    System.out.println("Polling directory every " + period / 1000 + " seconds.");
                    System.out.println("Thread sleep for " + Thsleep / 1000 + " seconds. Then attempt to parse web page.\n");
                    firstLoop = false;
                }

                // Sometimes their server takes longer than 60 seconds to parse replays.
                Element alert = doc.getElementsByClass("alert alert-info").first();
                if (alert != null) {
                    if (alert.text().contains("They will be processed shortly")) {
                        fileMgr.numFiles = fileMgr.numberOfFiles() + 1;
                        return;
                    }
                }

                // if no games in past 24 hours, then set all win rates to 0.
                if (!hasPlayedPast24Hrs || updatedServer) {
                    for (Element e : headings) {
                        if (e.toString().equals("<h2>24 Hours <strong>Quick</strong> Statistics</h2>")) {
                            String str = e.nextElementSibling().selectFirst("section").text();

                            if (str.equals("No games have been played")) {
                                WinRate.getInstance().update("null", "null");
                                buildFilePath(dirLinux);
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
                            String score = x.getElementsByTag("strong").first().text();
                            String matchup = x.getElementsByTag("label").first().text();

                            WinRate.getInstance().update(matchup, score);
                            buildFilePath(dirLinux);
                        }

                        if (winrate.size() < 3) {
                            winrates.matchup = RESET;
                            List<String> matchupFound = new ArrayList<>();
                            for (Element i : winrate) {
                                matchupFound.add(i.getElementsByTag("label").first().text());
                            }

                            List<String> missingMatchup = winrates.determineMissingMatchup(matchupFound);
                            for (String s : missingMatchup) {
                                buildFilePath(dirLinux, s);
                            }
                        }
                        return;
                    }
                }
            } else {
                System.out.println("Cannot connect to that web page.");
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("Cannot connect to that web page or the file path does not exist.");
            System.exit(0);
        }
    }

    void buildFilePath(String dir, String... exclude) throws IOException {
        switch (winrates.matchup) {
            case ZvP -> saveFile(dir, "ZvP.txt", winrates.score_ZvP);
            case ZvT -> saveFile(dir, "ZvT.txt", winrates.score_ZvT);
            case ZvZ -> saveFile(dir, "ZvZ.txt", winrates.score_ZvZ);
            case NULL -> {
                saveFile(dir, "ZvP.txt", winrates.score_reset);
                saveFile(dir, "ZvT.txt", winrates.score_reset);
                saveFile(dir, "ZvZ.txt", winrates.score_reset);
            }
            case RESET -> saveFile(dir, exclude[0] + ".txt", winrates.score_reset);
            default -> throw new IllegalArgumentException("Argument must be one of the three: ZvP, ZvT, ZvZ");
        }
    }

    void saveFile(String dir, String fileName, int[] score) throws IOException {
        fileMgr.save(dir + fileName, score);
    }
}
