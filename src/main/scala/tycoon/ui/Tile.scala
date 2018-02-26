package tycoon.ui

import tycoon.GridLocation

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.{Image,ImageView}


class Tile(tileset: Image, viewport: Rectangle2D) {

  private var _sprite : ImageView = new ImageView(tileset)
  _sprite.viewport = viewport

  def this(tile : Tile) {
    this(new Image(tile.sprite.image.get()), new Rectangle2D(tile.sprite.viewport.get()))
  }

  private var _displayed : Boolean = false

  def setScreenPos(x: Double, y: Double) = {
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
