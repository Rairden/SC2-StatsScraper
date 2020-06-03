If you play Starcraft 2, you can automatically upload your replays on https://sc2replaystats.com/

If you have an elite membership (perhaps $5-10/mo), you can use their plugin. Instead of paying, I wrote my own which simply downloads their web page showing game statistics.  

Here is what the web scraper looks like. And the game overlay using OBS.  

The script has functionality to switch between servers (since I switch from NA to EU server). Or you can type in `all` to show all games played in the past 24 hrs.  

![](img/SC2-stats-shell.png)

See right side of game (small overlay winrates)
![OBS overlay](img/SC2-stats-ingame.png)

## How to install

1. Place the application (SC2-StatsScraper.jar) anywhere on your system.
2. Run it from command prompt or powershell by typing this:

```sh
java -jar SC2-StatsScraper.jar
```

3. The first time you run the jar file, it will create a blank template `settings.cfg` file in your current directory. It needs to be customized and needs 3 lines of input from you.

My `settings.cfg` file is 20 lines long, but this would be a fully working cfg file like this, and order doesn't matter:

```sh
all=https://sc2replaystats.com/account/display/12345
win10=C:\Users\%USERNAME%\Documents
replays=E:\SC2\replayBackup\
```

The keywords on the left-hand side cannot change (all, win10, replays). Customize the paths on the right-hand side.

```sh
all=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
win10=xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
replays=xxxxxxxxxxxxxxxxxxxx
```

