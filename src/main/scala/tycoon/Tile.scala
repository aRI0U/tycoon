package tycoon

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.{Image, ImageView}

class Tile(width : Double, height : Double, tileset : Image, tileset_x : Double, tileset_y : Double) {
  private var view : ImageView = new ImageView(tileset)
  var displayed : Boolean = false
  view.viewport = new Rectangle2D(tileset_x, tileset_y, width, height)

  def getView : ImageView = view
}
