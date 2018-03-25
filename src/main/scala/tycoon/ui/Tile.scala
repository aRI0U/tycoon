package tycoon.ui

import tycoon.game.GridLocation

import scalafx.geometry.Rectangle2D
import scalafx.scene.CacheHint
import scalafx.scene.image.{Image,ImageView}

/*
class Tile(val tileset: Image, viewport: Rectangle2D, rotation: Double = 0) {

  private var _sprite : ImageView = new ImageView(tileset)
  _sprite.viewport = viewport
  _sprite.rotate = rotation
  // _sprite.cache = true
  // _sprite.cacheHint = CacheHint.Speed

  visible = true // tiles are all visible by default

  def this(tile: Tile) {
    this(new Image(tile.getView.image.value), new Rectangle2D(tile.getView.viewport.value), tile.getView.rotate.value)
  }

  // set position in the scene
  def setLayout(x: Double, y: Double) = {
    _sprite.layoutX = x
    _sprite.layoutY = y
  }

  // def move(dx, dy) dans Movable qui change aussi le screenPos

  def width : Int = viewport.width.toInt
  def height : Int = viewport.height.toInt

  def getView: ImageView = _sprite

  def visible : Boolean = _sprite.visible.value
  def visible_= (new_visible: Boolean) = _sprite.visible = new_visible

  // used in complement of visible to check if Renderable is in the scene whether it's displayed or not
  private var _inScene: Boolean = false
  def inScene: Boolean = _inScene
  def inScene_= (new_inScene: Boolean) = _inScene = new_inScene
}*/

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
