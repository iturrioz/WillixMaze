package com.me.game


import org.newdawn.slick._
import org.newdawn.slick.geom.{Line, Path}
import scala.Some

class Game extends BasicGame("Willix Maze") {

  val Width = 800
  val Height = 600

  var levelOption: Option[Level] = None
  var selectedLevel = Level.levels.head

  val input = new Input(Height)
  var pressedKey = -1

  var counter = 0l

  def render(container: GameContainer, g: Graphics) {
    State.current.render(container,g)
  }

  def init(container: GameContainer) {
    State.current = State.menu
  }

  def update(container: GameContainer, delta: Int) {
    State.current.update(container,delta)
  }
}

object Game {
  def main(args: Array[String]) {
    val game = new Game()
    val app = new AppGameContainer(game)
    app.setDisplayMode(game.Width, game.Height, false)
    app.setForceExit(false)
    app.start()
  }
}

