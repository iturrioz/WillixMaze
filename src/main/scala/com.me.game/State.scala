package com.me.game

import org.newdawn.slick.{Input, Graphics, GameContainer}

/*
  The abstract class of different states that the game can be during its lifecycle.
 */
abstract class State {
  val input = new Input(0)

  def render(container: GameContainer, g: Graphics)

  def update(container: GameContainer, delta: Int)

  def drawTextCenteredAt(g: Graphics, text: String, x: Int, y: Int) {
    val width = g.getFont.getWidth(text)
    val height = g.getFont.getHeight(text)
    g.drawString(text, x - width / 2, y - height / 2)
  }

}

object State {
  var current: State = null

  /*
    First screen state.
   */
  val menu = new State {
    private var startLevel = 0
    private var counter = System.currentTimeMillis()

    // Shows the title and the selected level.
    def render(container: GameContainer, g: Graphics) {
      drawTextCenteredAt(g, "Willix Maze", 400, 200)
      drawTextCenteredAt(g, "Press Enter to start level " + (Level.levels(startLevel).number), 400, 400)
      drawTextCenteredAt(g, "(Press UP or DOWN to change the level)", 400, 450)
    }

    // Updates the selected level at the beginning.
    def update(container: GameContainer, delta: Int) {
      if (counter < System.currentTimeMillis() - 100) {
        counter = System.currentTimeMillis()
        if (input.isKeyDown(Input.KEY_UP) != input.isKeyDown(Input.KEY_DOWN)) {
          if (input.isKeyDown(Input.KEY_UP)) startLevel = (startLevel + 1) min (Level.levels.size - 1)
          if (input.isKeyDown(Input.KEY_DOWN)) startLevel = (startLevel - 1) max 0
        }
      }
      if (input.isKeyDown(Input.KEY_ENTER)) {
        current = new Start(Level.levels(startLevel))
      }
    }
  }

  /*
    Level start screen.
   */
  class Start(levelInfo: Level.Info) extends State {

    // Shows level number and High score before starting
    def render(container: GameContainer, g: Graphics) {
      drawTextCenteredAt(g,"Level " + levelInfo.number,400,300)
      drawTextCenteredAt(g,"(Press spacebar to start)",400,500)
      g.drawString("High score: " + (if (levelInfo.getHighScore > 0) levelInfo.getHighScore else "-"), 500,100)
    }

    // Starts the game when space key is pressed
    def update(container: GameContainer, delta: Int) {
      if (input.isKeyDown(Input.KEY_SPACE)) {
        current = new Play(levelInfo)
      }
    }
  }

  /*
    Game state.
   */
  class Play(levelInfo: Level.Info) extends State {
    // Creates a random level with the given features
    val level = new Level(levelInfo, Level.Bounds(550, 550, 225, 25))
    private var counter = System.currentTimeMillis()
    def getRemainingSeconds(startTime: Long) = 60 - (System.currentTimeMillis() - startTime) / 1000

    // Draws the maze and the circle.
    def render(container: GameContainer, g: Graphics) {
      level.lines.foreach(g.draw)
      g.draw(level.circle)

      drawTextCenteredAt(g,"Level " + levelInfo.number,100,450)
      drawTextCenteredAt(g,"Time: " + (level.getRemainingSeconds max 0) + "s",100,500)
    }

    // Reads the arrow keys input and moves the circle through the maze.
    def update(container: GameContainer, delta: Int) {
      if (counter < System.currentTimeMillis() - 100) {
        counter = System.currentTimeMillis()
        // It moves if only one key is pressed.
        val oneKeyOnly = List(input.isKeyDown(Input.KEY_UP),
                              input.isKeyDown(Input.KEY_DOWN),
                              input.isKeyDown(Input.KEY_RIGHT),
                              input.isKeyDown(Input.KEY_LEFT)).count(d => d) == 1
        if (oneKeyOnly){
          if (input.isKeyDown(Input.KEY_UP)) level.moveCircle(0,-1)
          if (input.isKeyDown(Input.KEY_DOWN)) level.moveCircle(0,1)
          if (input.isKeyDown(Input.KEY_LEFT)) level.moveCircle(-1,0)
          if (input.isKeyDown(Input.KEY_RIGHT)) level.moveCircle(1,0)
        }
        // Checks if the maze is finished and if the time is over
        if (level.isFinished) current = new Finished(level)
        else if (level.getRemainingSeconds < 0 && !level.isFinished) current = new TimeOver(level)
      }
    }
  }

  /*
    Time over state.
   */
  class TimeOver(level: Level) extends State {

    // Shows the time is over message and stops the
    def render(container: GameContainer, g: Graphics) {
      level.lines.foreach(g.draw)
      g.draw(level.circle)

      drawTextCenteredAt(g,"Level " + level.info.number,100,450)
      drawTextCenteredAt(g,"Time Over",100,500)
      drawTextCenteredAt(g,"(Escape to go to menu)",100,550)
    }

    // Goes to menu when escape key is pressed
    def update(container: GameContainer, delta: Int) {
      if (input.isKeyDown(Input.KEY_ESCAPE)) {
        current = menu
      }
    }
  }

  /*
    Level finished state.
   */
  class Finished(level: Level) extends State {

    // Stores the score.
    val remainingTime = level.getRemainingSeconds
    val score = level.getPoints
    level.info.setHighScore(score)

    // Shows the score and the high score.
    def render(container: GameContainer, g: Graphics) {
      level.lines.foreach(g.draw)
      g.draw(level.circle)

      drawTextCenteredAt(g,"Level " + level.info.number,100,450)
      drawTextCenteredAt(g,"Time: " + (remainingTime max 0) + "s",100,500)
      drawTextCenteredAt(g,"Well done!",100,300)
      drawTextCenteredAt(g,"Score: " + score,100,350)
      drawTextCenteredAt(g,"(Enter to continue)",100,550)
      drawTextCenteredAt(g,"High score: " + level.info.getHighScore, 100,100)
    }

    // When enter is pressed, it goes to next level unless it was the last one. In that case it will show the end screen.
    def update(container: GameContainer, delta: Int) {
      if (input.isKeyDown(Input.KEY_ENTER)) {
        current = Level.levels.find(_.number > level.info.number) match {
          case Some(info) => new Start(info)
          case None => new TheEnd()
        }
      }
    }
  }

  /*
    End state.
   */
  class TheEnd() extends State {

    // Shows the end screen
    def render(container: GameContainer, g: Graphics) {
      drawTextCenteredAt(g,"The end ",400,250)
      drawTextCenteredAt(g,"Made by: dittbi",400,400)
      drawTextCenteredAt(g,"Willix team",400,450)
      drawTextCenteredAt(g,"(Escape to go to menu)",100,550)
    }

    // Goes to menu when escape key is pressed
    def update(container: GameContainer, delta: Int) {
      if (input.isKeyDown(Input.KEY_ESCAPE)) {
        current = menu
      }
    }
  }
}
