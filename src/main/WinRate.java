package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.Matchup.*;

enum Matchup {
    ZvP, ZvT, ZvZ, NULL, RESET;
}

public class WinRate {

    Matchup matchup;
    int[] score_ZvP;
    int[] score_ZvT;
    int[] score_ZvZ;
    int[] score_reset;

    int[] score_ZvP_reset;
    int[] score_ZvT_reset;
    int[] score_ZvZ_reset;

    static WinRate winRate = null;

    public WinRate() {
        this.matchup = NULL;
        this.score_ZvP = new int[2];
        this.score_ZvT = new int[2];
        this.score_ZvZ = new int[2];
        this.score_reset = new int[2];
    }

    public static WinRate getInstance() {
        if (winRate == null) {
            winRate = new WinRate();
        }
        return winRate;
    }

    public void update(String matchup, String score, boolean... resetAllGames) {
        String[] parsed = score.split("\\s");
        switch (matchup) {
            case "ZvP" -> {
                setScore(score_ZvP, parsed);
                resetGames(score_ZvP, score_ZvP_reset, resetAllGames);
                this.matchup = ZvP;
            }
            case "ZvT" -> {
                setScore(score_ZvT, parsed);
                resetGames(score_ZvT, score_ZvT_reset, resetAllGames);
                this.matchup = ZvT;
            }
            case "ZvZ" -> {
                setScore(score_ZvZ, parsed);
                resetGames(score_ZvZ, score_ZvZ_reset, resetAllGames);
                this.matchup = ZvZ;
            }
            default -> {
                Arrays.fill(score_ZvP, 0);
                Arrays.fill(score_ZvT, 0);
                Arrays.fill(score_ZvZ, 0);
                this.matchup = NULL;
            }
        }
    }

    private void resetGames(int[] score, int[] baseValue, boolean[] resetAllGames) {
        if (resetAllGames[0]) {
            setScoreReset(score, baseValue);
        }
    }

    private void setScore(int[] score, String[] parsed) {
        score[0] = Integer.parseInt(parsed[0]);
        score[1] = Integer.parseInt(parsed[2]);
    }

    private void setScoreReset(int[] score, int[] baseValue) {
        score[0] = score[0] - baseValue[0];
        score[1] = score[1] - baseValue[1];
    }

    public List<String> determineMissingMatchup(List<String> foundMatchups) {
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
