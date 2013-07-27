package com.me.game

import org.newdawn.slick.{Input, Graphics, GameContainer}

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
  var current: State = menu

  val menu = new State {
    private var startLevel = 0
    private var counter = System.currentTimeMillis()

    def render(container: GameContainer, g: Graphics) {
      drawTextCenteredAt(g, "Willix Maze", 400, 200)
      drawTextCenteredAt(g, "Press Enter to start level " + (Level.levels(startLevel).number), 400, 400)
      drawTextCenteredAt(g, "(Press UP or DOWN to change the level)", 400, 450)
    }

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

  class Start(levelInfo: Level.Info) extends State {
    def render(container: GameContainer, g: Graphics) {
      drawTextCenteredAt(g,"Level " + levelInfo.number,400,300)
      drawTextCenteredAt(g,"(Press spacebar to start)",400,500)
      g.drawString("High score: " + (if (levelInfo.getHighScore > 0) levelInfo.getHighScore else "-"), 500,100)
    }

    def update(container: GameContainer, delta: Int) {
      if (input.isKeyDown(Input.KEY_SPACE)) {
        current = new Play(levelInfo)
      }
    }
  }

  class Play(levelInfo: Level.Info) extends State {
    val level = new Level(levelInfo, Level.Bounds(550, 550, 225, 25))
    private var counter = System.currentTimeMillis()
    def getRemainingSeconds(startTime: Long) = 60 - (System.currentTimeMillis() - startTime) / 1000

    def render(container: GameContainer, g: Graphics) {
      level.lines.foreach(g.draw)
      g.draw(level.circle)

      drawTextCenteredAt(g,"Level " + levelInfo.number,100,400)
      drawTextCenteredAt(g,"Time: " + (level.getRemainingSeconds max 0) + "s",100,500)
    }

    def update(container: GameContainer, delta: Int) {
      if (counter < System.currentTimeMillis() - 100) {
        counter = System.currentTimeMillis()
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
        if (level.isFinished) current = new Finished(level)
        else if (level.getRemainingSeconds < 0 && !level.isFinished) current = new TimeOver(level)
      }
    }
  }
  class TimeOver(level: Level) extends State {
    def render(container: GameContainer, g: Graphics) {
      level.lines.foreach(g.draw)
      g.draw(level.circle)


      val remainingTime = level.getRemainingSeconds
      drawTextCenteredAt(g,"Level " + level.info.number,100,400)
      drawTextCenteredAt(g,"Time: " + (remainingTime max 0) + "s",100,500)
      drawTextCenteredAt(g,"Time Over",400,300)
      drawTextCenteredAt(g,"(Press Escape to go to main menu)",400,550)
    }
    def update(container: GameContainer, delta: Int) {
      if (input.isKeyDown(Input.KEY_ESCAPE)) {
        current = menu
      }
    }
  }
  class Finished(level: Level) extends State {
    val remainingTime = level.getRemainingSeconds
    val score = level.getPoints
    level.info.setHighScore(score)

    def render(container: GameContainer, g: Graphics) {
      level.lines.foreach(g.draw)
      g.draw(level.circle)

      drawTextCenteredAt(g,"Level " + level.info.number,100,450)
      drawTextCenteredAt(g,"Time: " + (remainingTime max 0) + "s",100,500)
      drawTextCenteredAt(g,"Well done! Score: " + score,100,300)
      drawTextCenteredAt(g,"(Press Enter to continue)",400,550)
      g.drawString("High score: " + level.info.getHighScore, 500,100)
    }
    def update(container: GameContainer, delta: Int) {
      if (input.isKeyDown(Input.KEY_ENTER)) {
        current = Level.levels.find(_.number > level.info.number) match {
          case Some(info) => new Start(info)
          case None => new TheEnd()
        }
      }
    }
  }
  class TheEnd() extends State {
    def render(container: GameContainer, g: Graphics) {
      drawTextCenteredAt(g,"The end ",400,250)
      drawTextCenteredAt(g,"Made by: dittbi",400,400)
      drawTextCenteredAt(g,"Willix team",400,450)
      drawTextCenteredAt(g,"(Press Escape to go to main menu)",400,550)
    }
    def update(container: GameContainer, delta: Int) {
      if (input.isKeyDown(Input.KEY_ESCAPE)) {
        current = menu
      }
    }
  }
}
