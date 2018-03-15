package tycoon.ui

import tycoon.game.GridLocation

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.{Image,ImageView}


class Tile(tileset: Image, viewport: Rectangle2D, rotation: Double = 0) {

  private var _sprite : ImageView = new ImageView(tileset)
  _sprite.viewport = viewport
  _sprite.rotate = rotation

  visible = false // tiles are not visible by default

  def this(tile: Tile) {
    this(new Image(tile.getView.image.get()), new Rectangle2D(tile.getView.viewport.get()), tile.getView.rotate.get())
  }
  /*def this(tileset: Image, viewport: Rectangle2D) {
    this(tileset, viewport, 0)
  }*/

  // set position in the scene
  def setLayout(x: Double, y: Double) = {
    _sprite.layoutX = x
    _sprite.layoutY = y
  }

  // def move(dx, dy) dans Movable qui change aussi le screenPos

  def width : Int = viewport.width.toInt
  def height : Int = viewport.height.toInt

  def getView: ImageView = _sprite

  def visible : Boolean = _sprite.visible.get()
  def visible_= (new_visible: Boolean) = _sprite.visible = new_visible
}

object Tile {
  private val tileset = new Image("file:src/main/resources/tileset.png")

  val square_width = 32
  val square_height = 32

  private def get_tile_rect(row: Int, col: Int, width: Int = 1, height: Int = 1) = {
    new Rectangle2D((col - 1) * square_width, (row - 1) * square_height, width * square_width, height * square_height)
  }

  val default = new Tile(tileset, get_tile_rect(0, 0))

  val tree = new Tile(tileset, get_tile_rect(4, 3))
  val rock = new Tile(tileset, get_tile_rect(4, 4))

  //vehicle stuff
  val locomotive = new Tile(tileset, get_tile_rect(3, 2))
  val passenger_wagon = new Tile(tileset, get_tile_rect(3, 1))
  val goods_wagon = new Tile(tileset, get_tile_rect(3, 3))
  val liquid_wagon = new Tile(tileset, get_tile_rect(6, 1))

  //structure tiles
  val town = new Tile(tileset, get_tile_rect(4, 1, width = 2))
  val mine = new Tile(tileset, get_tile_rect(4, 3))
  val factory_tile = new Tile(tileset, get_tile_rect(5, 1))
  val farm_tile = new Tile(tileset, get_tile_rect(5, 2, width = 2))

  // straight rails
  val straight_rail1 = new Tile(tileset, get_tile_rect(1, 1))

  // turning rails
  val turning_rail = new Tile(tileset, get_tile_rect(1, 2))

  // all the grass tiles, randomly spread on the map
  val grass = Array(
    new Tile(tileset, get_tile_rect(1, 3)),
    new Tile(tileset, get_tile_rect(1, 4)),
    new Tile(tileset, get_tile_rect(2, 1)),
    new Tile(tileset, get_tile_rect(2, 2)),
    new Tile(tileset, get_tile_rect(2, 3)),
    new Tile(tileset, get_tile_rect(2, 4))
  )
}
