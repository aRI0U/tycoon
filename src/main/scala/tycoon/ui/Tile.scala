package tycoon.ui

import tycoon.game.GridLocation

import scalafx.geometry.Rectangle2D
import scalafx.scene.CacheHint
import scalafx.scene.image.{Image,ImageView}


class Tile(row: Int, col: Int, val width: Int = 1, val height: Int = 1) {
  // source rectangle's coordinates and size in Tile.tileset
  val sx = (col - 1) * Tile.SquareWidth
  val sy = (row - 1) * Tile.SquareHeight
  val sw = width * Tile.SquareWidth
  val sh = height * Tile.SquareHeight
}

object Tile {
  val tileset = new Image("file:src/main/resources/tileset.png")

  val SquareWidth = 32
  val SquareHeight = 32

  def getImageView(t: Tile): ImageView = {
    val img = new ImageView(tileset)
    img.viewport = new Rectangle2D(t.sx, t.sy, t.sw, t.sh)
    img
  }

  val default = new Tile(0, 0)
  val tree = new Tile(3, 4)
  val rock = new Tile(4, 4)
  val plainWater = new Tile(5, 5)

  //vehicle stuff
  val locomotiveL = new Tile(3, 5)
  val locomotiveB = new Tile(3, 2)
  val locomotiveR = new Tile(4, 5)
  val locomotiveT = new Tile(3, 6)
  val passengerWagonT = new Tile(4, 6)
  val passengerWagonB = new Tile(3, 1)
  val passengerWagonL = new Tile(5, 6)
  val passengerWagonR = new Tile(6, 6)
  val goodsWagonR = new Tile(7, 5)
  val goodsWagonB = new Tile(3, 3)
  val goodsWagonL = new Tile(7, 4)
  val goodsWagonT = new Tile(7, 6)

  val liquid_wagon = new Tile(6, 1)

  //structure tiles
  val town = new Tile(4, 1, width = 2)
  val mine = new Tile(4, 3)
  val factory = new Tile(5, 1)
  val farm = new Tile(5, 2, width = 2)
  val airport = new Tile(6, 3)

  val straightRailBT = new Tile(1, 1)
  val straightRailLR = new Tile(2, 6)
  val turningRailBR = new Tile(1, 2)
  val turningRailBL = new Tile(1, 6)
  val turningRailTR = new Tile(1, 5)
  val turningRailTL = new Tile(2, 5)


  val grassAndGround = Array(
    new Tile(1, 3),
    new Tile(1, 4),
    new Tile(2, 1),
    new Tile(2, 2),
    new Tile(2, 3),
    new Tile(2, 4),
    // new Tile(3, 4),
    // new Tile(4, 4)
  )
}
