package com.me.game

import scala.util.Random
import org.newdawn.slick.geom.{Circle, Line}

class Level(val info: Level.Info, bounds: Level.Bounds) {
  implicit def random[T](elements: List[T]) = new {
    def randomElement: T = elements(Random.nextInt(elements.size))
  }

  def width = info.width

  def height = info.height

  def cellWidth = bounds.widthPixels / width

  def cellHeight = bounds.heightPixels / height

  private var finished = false

  def isFinished = finished

  private val startTime = System.currentTimeMillis()
  def getRemainingSeconds = info.solveTime - (System.currentTimeMillis() - startTime) / 1000

  def getPoints = info.solveTime * 1000 - (System.currentTimeMillis() - startTime)

  private def radius = (cellHeight min cellWidth) / 4f

  private var circlePosition = Position(0, 0)
  def circle =
    new Circle(bounds.xOffset + cellWidth / 2f * (circlePosition.x), bounds.yOffset + cellHeight / 2f * (circlePosition.y), radius)

  // Maze generation. Recursive backtracker (http://en.wikipedia.org/wiki/Maze_generation_algorithm)
  private val maze = {
    val initialMaze = Array.tabulate(width * 2 + 1, height * 2 + 1)((_, _) => 1)
    val initial = Random.nextInt(height) * 2 + 1
    circlePosition = Position(-1, initial)
    initialMaze(0)(initial) = 0
    initialMaze(1)(initial) = 0
    initialMaze(width * 2)(Random.nextInt(height) * 2 + 1) = 0

    val firstCell = Position(1, initial)
    var stack = List(firstCell)
    addCell(firstCell)

    def addCell(cell: Position) {
      val newNeighbours = cell.getNeighbours(position => initialMaze(position.x)(position.y) == 1)
      if (newNeighbours.nonEmpty) {
        val neighbour = newNeighbours.randomElement
        initialMaze(neighbour.x)(neighbour.y) = 0
        stack = neighbour :: stack
        removeWallBetweenCells(neighbour, cell)
        addCell(neighbour)
      } else {
        if (stack.nonEmpty) {
          val nextPosition = stack.head
          stack = stack.tail
          addCell(nextPosition)
        }
      }
    }
    def removeWallBetweenCells(cell1: Position, cell2: Position) {
      if (cell1.x != cell2.x) initialMaze(cell1.x - (cell1.x - cell2.x) / 2)(cell1.y) = 0
      else initialMaze(cell1.x)(cell1.y - (cell1.y - cell2.y) / 2) = 0
    }
    initialMaze
  }

  // Create {@Link Line} type walls.
  val lines = maze.zipWithIndex.map {
    case (column, x) =>
      column.zipWithIndex.map {
        case (value, y) =>
          if (value == 1 && x % 2 != y % 2) {
            if (x % 2 == 1 && y % 2 == 0) {
              Some(new Line(bounds.xOffset + bounds.widthPixels / width * (x - 1) / 2, bounds.yOffset + bounds.heightPixels / height * y / 2,
                bounds.xOffset + bounds.widthPixels / width * ((x - 1) / 2 + 1), bounds.yOffset + bounds.heightPixels / height * y / 2))
            } else {
              Some(new Line(bounds.xOffset + bounds.widthPixels / width * x / 2, bounds.yOffset + bounds.heightPixels / height * (y - 1) / 2,
                bounds.xOffset + bounds.widthPixels / width * x / 2, bounds.yOffset + bounds.heightPixels / height * ((y - 1) / 2 + 1)))
            }
          } else None
      }.flatten
  }.flatten.toList

  // Check if the movement is possible and move it.
  def moveCircle(x: Int, y: Int) {
    if (x != y && (x == 0 || y == 0)) {
      val wall = Position(x + circlePosition.x, y + circlePosition.y)
      if (wall.y > 0 && wall.y < height * 2 && ((wall.x > 0) || (wall.x == 0 && circlePosition.x == -1)) &&
        maze(wall.x)(wall.y) == 0) {
        circlePosition = Position(wall.x + x, wall.y + y)
        if (circlePosition.x == width * 2 + 1) finished = true
      }
    }
  }

  /*
    A class that represents the position of walls and cells
   */
  case class Position(x: Int, y: Int) {
    def getWalls(condition: (Position) => Boolean = (Position) => true): List[Position] =
      List((0, 1), (0, -1), (1, 0), (-1, 0)).map {
        case (dx, dy) => Position(x + dx, y + dy)
      }
        .filter(pos => pos.x > 0 && pos.x <= width * 2 && pos.y > 0 && pos.y <= height * 2 && condition(pos))

    def getNeighbours(condition: (Position) => Boolean = (Position) => true): List[Position] =
      List((0, 2), (0, -2), (2, 0), (-2, 0)).map {
        case (dx, dy) => Position(x + dx, y + dy)
      }
        .filter(pos => pos.x > 0 && pos.x <= width * 2 && pos.y > 0 && pos.y <= height * 2 && condition(pos))

    def value = maze(x)(y)
  }

}

object Level {

  /*
    Level difficulty info
   */
  case class Info(number: Int, width: Int, height: Int, solveTime: Int) {
    private var highScore = 0l

    def getHighScore = highScore

    def setHighScore(score: Long) {
      highScore = highScore max score
    }
  }

  // List of levels
  val levels = List(
    Info(1, 10, 10, 60),
    Info(2, 12, 12, 60),
    Info(3, 14, 14, 60),
    Info(4, 16, 16, 60),
    Info(5, 18, 18, 60),
    Info(6, 20, 20, 120),
    Info(7, 22, 22, 120),
    Info(8, 24, 24, 120),
    Info(9, 26, 26, 120),
    Info(10, 28, 28, 120),
    Info(11, 30, 30, 180),
    Info(12, 32, 32, 180),
    Info(13, 34, 34, 180),
    Info(14, 37, 37, 180),
    Info(15, 40, 40, 180))

  /*
    Rendering bounds
   */
  case class Bounds(widthPixels: Float, heightPixels: Float, xOffset: Int, yOffset: Int)

}