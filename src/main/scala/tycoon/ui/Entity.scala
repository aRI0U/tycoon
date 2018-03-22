package tycoon.ui

import tycoon.game.GridLocation
import tycoon.game.GridRectangle

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.ImageView
import scala.collection.mutable.ListBuffer
import scalafx.beans.property.StringProperty

trait Entity {
  private var _tile: Tile = Tile.default
  private var _gridRect: GridRectangle = new GridRectangle(new GridLocation(0, 0), 1, 1)

  private val _printData: ListBuffer[(String, StringProperty)] = ListBuffer()
  def printData = _printData

  def getView: ImageView = tile.getView

  def gridIntersects(other: Entity): Boolean = gridRect.intersects(other.gridRect)
  def gridContains(pos: GridLocation): Boolean = gridRect.contains(pos)

  def tile: Tile = _tile
  def tile_= (new_tile: Tile) = {
    _tile = new_tile
    _gridRect = new GridRectangle(new GridLocation(0, 0), tile.width / Tile.square_width, tile.height / Tile.square_height)
  }

  def gridRect: GridRectangle = _gridRect

  // size in pixels
  def width: Int = tile.width
  def height: Int = tile.height

  def gridWidth: Int = tile.width / Tile.square_width
  def gridHeight: Int = tile.height / Tile.square_height

  def getPos: GridLocation = gridRect.pos

  // set pos on grid (in cases)
  def setPos(pos: GridLocation) {
    _gridRect = new GridRectangle(pos, tile.width / Tile.square_width, tile.height / Tile.square_height)
  }
  // set pos on scene (in pixels)
  def setLayout(x: Double, y: Double) {
    tile.setLayout(x, y)
  }

  def visible: Boolean = tile.visible
  def visible_= (new_visible: Boolean) = tile.visible = new_visible
  def toggleVisible: Unit = visible = !visible

  def inScene: Boolean = tile.inScene
  def inScene_= (new_inScene: Boolean) = tile.inScene = new_inScene
}
