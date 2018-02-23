package traits

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.{Image, ImageView}


trait Renderable {
  var tileset : Image
  var width : Double
  var height : Double
  var tileset_x : Double
  var tileset_y : Double
  var view : ImageView = new ImageView(tileset)
  var displayed : Boolean = false

  setViewRect(tileset_x, tileset_y, width, height)

  def setPos(layout_x : Double, layout_y : Double) = {
    view.layoutX = layout_x
    view.layoutY = layout_y
  }

  /*def posX = view.layoutX
  def posY = view.layoutY*/

  def setViewRect(tileset_x : Double, tileset_y : Double, width : Double, height : Double) = {
    view.viewport = new Rectangle2D(tileset_x, tileset_y, width, height)
  }

  def toggleVisible() = {
    displayed = !displayed
  }

  def getView : ImageView = view
}
