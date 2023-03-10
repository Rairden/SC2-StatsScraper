package main;

import java.util.Arrays;

class WinRate {

    int[] zvt;
    int[] zvp;
    int[] zvz;
    String matchup;

    public WinRate() {
        int[] zvp = {0, 0};
        int[] zvt = {0, 0};
        int[] zvz = {0, 0};
        this.zvt = zvt;
        this.zvp = zvp;
        this.zvz = zvz;
    }

    WinRate(String matchup, int wins, int losses) {
        this();
        this.matchup = matchup;
        if (matchup.equals("ZvP")) {
            this.zvp[0] = wins;
            this.zvp[1] = losses;
        } else if (matchup.equals("ZvT")) {
            this.zvt[0] = wins;
            this.zvt[1] = losses;
        } else if (matchup.equals("ZvZ")) {
            this.zvz[0] = wins;
            this.zvz[1] = losses;
        }
    }

    void resetWinRates() {
        Arrays.fill(zvp, 0);
        Arrays.fill(zvt, 0);
        Arrays.fill(zvz, 0);
    }
}
