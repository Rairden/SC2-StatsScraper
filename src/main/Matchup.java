package main;

enum Matchup {
    ZvP("ZvP"), ZvT("ZvT"), ZvZ("ZvZ"), NULL("");

    public final String matchup;

    Matchup(String matchup) {
        this.matchup = matchup;
    }
}
