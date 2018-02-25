package tycoon.ui

import tycoon.GridLocation

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.{Image,ImageView}


class Tile(tileset: Image, viewport: Rectangle2D) {

  //val tileset = new Image("file:src/main/resources/tileset.png")
  var sprite : ImageView = new ImageView(tileset)
  sprite.viewport = viewport
  //view.viewport = new Rectangle2D(0,0,32,32)

  //var view : ImageView = new ImageView(sprite)

  def this(tile : Tile) {
    this(new Image(tile.sprite.image.get()), new Rectangle2D(tile.sprite.viewport.get()))
  }
  // sprite size
  //def width : Double = sprite.viewport.width
  //def height : Double = sprite.viewport.height // /!\ voir si ça permet de changer ou si c'est juste un getter

  // screen-relative coordinates (top-left corner is (0;0))
  //def layoutX : Double = sprite.layoutX
  //def layoutY : Double = sprite.layoutY

  // absolute coordinates
  //var gridPos : GridLocation

  def setPos(x: Double, y: Double) = {
    sprite.layoutX = x
    sprite.layoutY = y
  }

  def width : Int = viewport.width.toInt
  def height : Int = viewport.height.toInt

  def getView : ImageView = sprite

  var displayed : Boolean = false
}
