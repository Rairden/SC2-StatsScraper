package main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

// https://www.youtube.com/watch?v=0s8O7jfy3c0
// https://stackoverflow.com/questions/17315886/extract-and-group-elements-together-with-jsoup

public class SC2Stats extends TimerTask {

    WinRate winrates;
    FileManager fileManager;
    boolean hasPlayedPast24Hrs = false;

    public SC2Stats() {
        winrates = new WinRate();
        fileManager = new FileManager();
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        SC2Stats timertask = new SC2Stats();
        timer.schedule(timertask, 0, 5000);   // 1000 = 1 second
    }

    @Override
    public void run() {
        try {
            // Document doc = Jsoup.connect("https://sc2replaystats.com/account/display/49324/0/195960-2794640/1v1/AutoMM/43/").userAgent("Chrome/81.0").get();
            Document doc = Jsoup.connect("http://localhost/webscraper/3games-past24-bothAccounts.html").userAgent("Chrome/81.0").get();
            Elements tmp = doc.select("h2");

            // if no games in past 24 hours, then set all win rates to 0.
            if (!hasPlayedPast24Hrs) {
                for (Element e : tmp) {
                    if (e.toString().equals("<h2>24 Hours <strong>Quick</strong> Statistics</h2>")) {
                        String str = e.nextElementSibling().selectFirst("section").text();

                        if (str.equals("No games have been played")) {
                            winrates.resetWinRates();
                            return;
                        } else {
                            break;
                        }
                    }
                }
            }

            for (Element e : tmp) {
                if (e.toString().equals("<h2>24 Hours <strong>Race </strong> Statistics</h2>")) {
                    hasPlayedPast24Hrs = true;
                    Elements winrate = e.nextElementSibling().select("div.col-md-2");
                    for (Element x : winrate) {
                        String[] split = x.getElementsByTag("strong").first().text().split("\\s");
                        String matchup = x.getElementsByTag("label").first().text();

                        int wins = Integer.parseInt(split[0]);
                        int losses = Integer.parseInt(split[2]);

                        WinRate wr = new WinRate(matchup, wins, losses);
                        String dir = "/home/erik/scratch/SC2-scraper/";
                        // String dir = "C:\\Users\\Erik\\Documents\\OBS-win10-sc2\\sc2-Streaming\\winrate\\";
                        buildFilePath(dir, wr);
                    }
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Cannot connect to that web page or the file path does not exist.");
            System.exit(0);
        }
    }

    void buildFilePath(String dir, WinRate wr) throws IOException {
        switch (wr.matchup) {
            case "ZvP" -> saveFile(dir, "ZvP_Zwins.txt", "ZvP_Pwins.txt", wr.zvp);
            case "ZvT" -> saveFile(dir, "ZvT_Zwins.txt", "ZvT_Twins.txt", wr.zvt);
            case "ZvZ" -> saveFile(dir, "ZvZ_wins.txt", "ZvZ_losses.txt", wr.zvz);
            default -> throw new IllegalArgumentException("Argument must be one of the three: ZvP, ZvT, ZvZ");
        }
    }

    void saveFile(String dir, String fileName1, String fileName2, int[] ZvX) throws IOException {
        StringBuilder sb1 = new StringBuilder(dir);
        StringBuilder sb2 = new StringBuilder(dir);
        sb1.append(fileName1);
        sb2.append(fileName2);
        fileManager.save(sb1.toString(), ZvX[0]);
        fileManager.save(sb2.toString(), ZvX[1]);
    }
}
