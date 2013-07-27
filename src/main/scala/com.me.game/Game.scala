package com.me.game


import org.newdawn.slick._

class Game extends BasicGame("Willix Maze") {

  val Width = 800
  val Height = 600

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
    app.setShowFPS(false)
    app.start()
  }
}

