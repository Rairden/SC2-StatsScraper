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

    public SC2Stats() {
        winrates = new WinRate();
        fileManager = new FileManager();
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        SC2Stats timertask = new SC2Stats();
        timer.schedule(timertask, 0, 100000);   // 1000 = 1 second
    }

    @Override
    public void run() {
        try {
            // both accounts NA/EU S43
            // Document doc2 = Jsoup.connect("https://sc2replaystats.com/account/display/49324/0/195960-2794640/1v1/AutoMM/43/").userAgent("Chrome/81.0").get();

            // default account (Gixx)
            Document doc = Jsoup.connect("https://sc2replaystats.com/account/display/49324").userAgent("Chrome/81.0").get();
            Elements tmp = doc.select("h2");

            // if no games in past 24 hours, then set all win rates to 0.
            for (Element e : tmp) {
                if (e.toString().equals("<h2>24 Hours <strong>Quick</strong> Statistics</h2>")) {
                    String str = e.nextElementSibling().selectFirst("section").text();

                    if (str.equals("No games have been played")) {
                        winrates.resetWinRates();
                        break;
                    }
                }
            }

            for (Element e : tmp) {
                if (e.toString().equals("<h2>24 Hours <strong>Race </strong> Statistics</h2>")) {
                    Elements winrate = e.nextElementSibling().select("div.col-md-2");
                    for (Element x : winrate) {
                        String[] split = x.getElementsByTag("strong").first().text().split("\\s");
                        String matchup = x.getElementsByTag("label").first().text();

                        int wins = Integer.parseInt(split[0]);
                        int losses = Integer.parseInt(split[2]);

                        WinRate wr = new WinRate(matchup, wins, losses);
                        StringBuilder sb = new StringBuilder();
                        sb.append("C:\\Users\\Erik\\Documents\\OBS-win10-sc2\\sc2-Streaming\\winrate\\");
                        buildFilePath(sb, wr);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Cannot connect to that webpage or invalid path for win10/linux.");
            System.exit(1);
        }
    }

    void buildFilePath(StringBuilder directory, WinRate wr) throws IOException {
        StringBuilder fullPath1 = new StringBuilder();
        StringBuilder fullPath2 = new StringBuilder();
        fullPath1.append(directory);
        fullPath2.append(directory);

        if (wr.matchup.equals("ZvP")) {
            fullPath1.append("ZvP_Zwins.txt");
            fullPath2.append("ZvP_Pwins.txt");
            fileManager.save(fullPath1.toString(), wr.zvp[0]);
            fileManager.save(fullPath2.toString(), wr.zvp[1]);
        } else if (wr.matchup.equals("ZvT")) {
            fullPath1.append("ZvT_Zwins.txt");
            fullPath2.append("ZvT_Twins.txt");
            fileManager.save(fullPath1.toString(), wr.zvt[0]);
            fileManager.save(fullPath2.toString(), wr.zvt[1]);
        } else if (wr.matchup.equals("ZvZ")) {
            fullPath1.append("ZvZ_wins.txt");
            fullPath2.append("ZvZ_losses.txt");
            fileManager.save(fullPath1.toString(), wr.zvz[0]);
            fileManager.save(fullPath2.toString(), wr.zvz[1]);
        }
    }
}
