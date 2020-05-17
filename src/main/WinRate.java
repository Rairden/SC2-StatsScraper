package main;

final class WinRate {

    String matchup = "";
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
        this.matchup = matchup;
        switch (matchup) {
            case "ZvP" -> this.score_zvp = score;
            case "ZvT" -> this.score_zvt = score;
            case "ZvZ" -> this.score_zvz = score;
            default -> {
                this.score_zvp = "0 - 0";
                this.score_zvt = "0 - 0";
                this.score_zvz = "0 - 0";
            }
        }
    }
}
