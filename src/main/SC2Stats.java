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

    public SC2Stats() {
        winrates = new WinRate();
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new SC2Stats(), 0, 30000);   // 5000 = 5 seconds

    }

    @Override
    public void run() {
        try {
            // Document doc = Jsoup.connect("https://sc2replaystats.com/account/display/49324/0/195960/1v1/All/43/").userAgent("Mozilla/17.0").get();
            // Elements temp = doc2.select("div.row.text-center.countTo div.col-md-2");    // works (select inner div)

            Document doc2 = Jsoup.connect("http://localhost/webscraper/2games-past24.html").userAgent("Chrome/81.0").get();
            Document doc3 = Jsoup.connect("http://localhost/webscraper/nogames24hrs.html").userAgent("Chrome/81.0").get();
            Elements tmp2 = doc2.select("h2");

            // if no games in past 24 hours, then set all win rates to 0.
            for (Element e : tmp2) {
                if (e.toString().equals("<h2>24 Hours <strong>Quick</strong> Statistics</h2>")) {
                    String str = e.nextElementSibling().selectFirst("section").text();

                    if (str.equals("No games have been played")) {
                        winrates.resetWinRates();
                    }
                }
            }

            for (Element e : tmp2) {
                if (e.toString().equals("<h2>24 Hours <strong>Race </strong> Statistics</h2>")) {
                    Elements winrate = e.nextElementSibling().select("div.col-md-2");
                    for (Element x : winrate) {
                        String[] split = x.getElementsByTag("strong").first().text().split("\\s");
                        String matchup = x.getElementsByTag("label").first().text();

                        int wins = Integer.parseInt(split[0]);
                        int losses = Integer.parseInt(split[2]);

                        WinRate wr = new WinRate(matchup, wins, losses);


                        System.out.printf("%s: %d - %d\n", matchup, wins, losses);
                    }
                }
            }
        } catch (IOException e) {
            return;
        }
    }
}
