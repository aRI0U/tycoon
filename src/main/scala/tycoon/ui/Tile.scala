package tycoon.ui

import tycoon.GridLocation

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.{Image,ImageView}


class Tile(tileset: Image, viewport: Rectangle2D, rotation: Double) {

  private var _sprite : ImageView = new ImageView(tileset)
  _sprite.viewport = viewport
  _sprite.rotate = rotation

  def this(tile: Tile) {
    this(new Image(tile.sprite.image.get()), new Rectangle2D(tile.sprite.viewport.get()), tile.sprite.rotate.get())
  }
  def this(tileset: Image, viewport: Rectangle2D) {
    this(tileset, viewport, 0)
  }

  private var _displayed : Boolean = false

  // set position in the scene
  def setLayout(x: Double, y: Double) = {
    sprite.layoutX = x
    sprite.layoutY = y
  }

  // def move(dx, dy) dans Movable qui change aussi le screenPos

  def width : Int = viewport.width.toInt
  def height : Int = viewport.height.toInt

  def getView : ImageView = sprite

  def displayed : Boolean = _displayed
  def displayed_= (new_displayed: Boolean) = _displayed = new_displayed

  def sprite : ImageView = _sprite
  def sprite_= (new_sprite: ImageView) = _sprite = new_sprite


}

object Tile {
  private val tileset = new Image("file:src/main/resources/tileset.png")

  val square_width = 32
  val square_height = 32

  private def get_tile_rect(row: Int, col: Int, width: Int = 1, height: Int = 1) = {
    new Rectangle2D((col - 1) * square_width, (row - 1) * square_height, width * square_width, height * square_height)
  }

  val tree = new Tile(tileset, get_tile_rect(4, 3))
  val rock = new Tile(tileset, get_tile_rect(4, 4))
  val town = new Tile(tileset, get_tile_rect(4, 1, width = 2))
  val mine = new Tile(tileset, get_tile_rect(4, 3))
  val locomotive = new Tile(tileset, get_tile_rect(3, 2))

  // straight rails
  val straight_rail1 = new Tile(tileset, get_tile_rect(1, 1))
  val straight_rail2 = new Tile(tileset, get_tile_rect(1, 1), 90)

  // turning rails
  val turning_rail = new Tile(tileset, get_tile_rect(1, 2))
  val turning_rail2 = new Tile(tileset, get_tile_rect(1, 2), 90) // ??
  val turning_rail3 = new Tile(tileset, get_tile_rect(1, 2), 90) // ??
  val turning_rail4 = new Tile(tileset, get_tile_rect(1, 2), 90) // ??

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
