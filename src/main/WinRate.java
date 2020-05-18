package main;

import static main.Matchup.*;

final class WinRate {

    Matchup matchup;
    String score_zvp;
    String score_zvt;
    String score_zvz;

    static WinRate winRate = null;

    static WinRate getInstance() {
        if (winRate == null) {
            winRate = new WinRate();
        }
        return winRate;
    }

    void update(String matchup, String score) {
        switch (matchup) {
            case "ZvP" -> {
                this.score_zvp = score;
                this.matchup = ZvP;
            }
            case "ZvT" -> {
                this.score_zvt = score;
                this.matchup = ZvT;
            }
            case "ZvZ" -> {
                this.score_zvz = score;
                this.matchup = ZvZ;
            }
            default -> {
                this.score_zvp = "0 - 0";
                this.score_zvt = "0 - 0";
                this.score_zvz = "0 - 0";
                this.matchup = NULL;
            }
        }
    }
}
