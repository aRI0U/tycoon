package tycoon

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.{Image, ImageView}

import traits.Renderable

class Tile(var width : Double, var height : Double, var tileset : Image, var tileset_x : Double, var tileset_y : Double) extends Renderable {
  setViewRect(tileset_x, tileset_y, width, height)
}
