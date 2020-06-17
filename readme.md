If you play Starcraft 2, you can automatically upload your replays on https://sc2replaystats.com/

If you have an elite membership (perhaps $5-10/mo), you can use their plugin. Instead of paying, I wrote my own which simply downloads their web page showing game statistics.

Here is what the web scraper looks like. And the game overlay using OBS.

The script has functionality to switch between servers (since I switch from NA to EU server). Or you can type in `all` to show all games played in the past 24 hrs.

![](img/SC2-stats-shell.png)

See right side of game (small overlay winrates)  
![OBS overlay](img/SC2-stats-ingame.png)

## How to install

1. Install Java 14 - Java(TM) SE Runtime Environment (build 14.0.1+7)
2. Go to [sc2replaystats.com](https://sc2replaystats.com/), signup, and install their automatic replay upload tool.
3. Place the application (SC2-StatsScraper.jar) anywhere on your system.
4. Run it from command prompt or powershell:

```sh
java -jar SC2-StatsScraper.jar
```

5. The first time you run the jar file, it will create a blank template `settings.cfg` file in your current directory. Before the program will run, it needs 2 lines of input from you in `settings.cfg`.

My `settings.cfg` file is 20 lines long, but this would be a fully working config file like this, and order doesn't matter:

```sh
scores=
replays=E:\SC2\replayBackup\
all=https://sc2replaystats.com/account/display/12345
```

The keywords on the left-hand side cannot change (scores, replays, all). Customize the paths on the right-hand side of the equals sign.

```sh
replays=xxxxx
all=xxxxx
```

The program works by detecting if SC2 (or scelight) saved a new `.SC2Replay` file to your hard drive.

You have two options (#2 works best):

1) Point `replays=` to your SC2 replay folder
2) Configure scelight to auto-backup new replays to a custom directory (so both NA/EU games are saved to one folder)

```
replays=C:\Users\<insertUSER>\Documents\StarCraft II\Accounts\12345678\1-S2-1-1234567\Replays\Multiplayer\
replays=E:\SC2\replayBackup\
```

