package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.Matchup.*;

enum Matchup {
    ZvP, ZvT, ZvZ, NULL, RESET;
}

final class WinRate {

    Matchup matchup;
    int[] score_ZvP;
    int[] score_ZvT;
    int[] score_ZvZ;
    int[] score_reset;

    static WinRate winRate = null;

    public WinRate() {
        this.matchup = NULL;
        this.score_ZvP = new int[2];
        this.score_ZvT = new int[2];
        this.score_ZvZ = new int[2];
        this.score_reset = new int[2];
    }

    static WinRate getInstance() {
        if (winRate == null) {
            winRate = new WinRate();
        }
        return winRate;
    }

    void update(String matchup, String score) {
        String[] split = score.split("\\s");
        switch (matchup) {
            case "ZvP" -> {
                setScore(this.score_ZvP, split);
                this.matchup = ZvP;
            }
            case "ZvT" -> {
                setScore(this.score_ZvT, split);
                this.matchup = ZvT;
            }
            case "ZvZ" -> {
                setScore(this.score_ZvZ, split);
                this.matchup = ZvZ;
            }
            default -> {
                Arrays.fill(this.score_ZvP, 0);
                Arrays.fill(this.score_ZvT, 0);
                Arrays.fill(this.score_ZvZ, 0);
                this.matchup = NULL;
            }
        }
    }

    void setScore(int[] score, String[] split) {
        score[0] = Integer.parseInt(split[0]);
        score[1] = Integer.parseInt(split[2]);
    }

    List<String> determineMissingMatchup(List<String> foundMatchups) {
        List<String> matchup = new ArrayList<>();
        if (!foundMatchups.contains("ZvP")) {
            matchup.add("ZvP");
        }
        if (!foundMatchups.contains("ZvT")) {
            matchup.add("ZvT");
        }
        if (!foundMatchups.contains("ZvZ")) {
            matchup.add("ZvZ");
        }
        return matchup;
    }
}
