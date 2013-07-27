WillixMaze
==========

FOSS coding for Euskal Encounter 2013

### Quickstart

Pre-requisites:
* Git, Java and Maven working
* Slick 2D depends on `jnlp.jar` which ships with the "Demo and Samples" package of the JDK. This package must be installed to build the project. With Java 6 it's a box to tick when installing the JDK, and with Java 7 it's a separate download.
* Won't work with OpenJDK for the reason above (Can be solved by providing `jnlp.jar` separately)

#### Clone and build the project

```
git clone git://github.com/iturrioz/WillixMaze.git
cd WillixMaze
mvn clean package
```

#### Run the game
Unzip the file target/game-0.0.1-SNAPSHOT-release.zip and run `game.sh` (Linux) or `game.bat` (Windows).


#### How to play
From the main menu screen you can select the maze level by pressing UP and Down arrow keys. Once you have selected one press enter to start it.

Then it will show the info screen of the level. Press spacebar to start the level.

In each level you have to move the circle, pressing the arrow keys, through the maze to reach the exit in the right part before you run out of time.



