package tycoon.ui

import tycoon.game.GridLocation
import tycoon.game.GridRectangle

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.ImageView
import scala.collection.mutable.ListBuffer
import scalafx.beans.property.StringProperty

abstract class Renderable(private var pos: GridLocation) {
  private var _tile: Tile = Tile.default
  private var _gridRect: GridRectangle = new GridRectangle(pos, 1, 1)
  private val _printData: ListBuffer[(String, StringProperty)] = ListBuffer()
  var visible: Boolean = true

  def tile: Tile = _tile
  def gridRect: GridRectangle = _gridRect
  def gridPos: GridLocation = _gridRect.pos
  def printData = _printData

  def tile_= (newTile: Tile) = {
    _tile = newTile
    _gridRect.cols = tile.width
    _gridRect.rows = tile.height
  }
  def gridPos_= (newGridPos: GridLocation) = _gridRect.pos = newGridPos // old setPos

  def gridIntersects(other: Renderable): Boolean = gridRect.intersects(other.gridRect)
  def gridContains(pos: GridLocation): Boolean = gridRect.contains(pos)



  // size in pixels ;; nécessaire ?
  def width: Int = tile.width
  def height: Int = tile.height

  def gridWidth: Int = tile.width * Tile.SquareWidth
  def gridHeight: Int = tile.height * Tile.SquareHeight


}


/*


trait Renderable {
  protected var _tile: Tile
  private var _gridRect: GridRectangle = _

  private val _printData: ListBuffer[(String, StringProperty)] = ListBuffer()
  def printData = _printData

  def getView: ImageView = tile.getView

  def gridIntersects(other: Renderable): Boolean = gridRect.intersects(other.gridRect)
  def gridContains(pos: GridLocation): Boolean = gridRect.contains(pos)

  def tile: Tile = _tile
  def tile_= (new_tile: Tile) = {
    _tile = new_tile
    _gridRect = new GridRectangle(_gridRect.pos, tile.width / Tile.SquareWidth, tile.height / Tile.SquareHeight)
  }

  def gridRect: GridRectangle = _gridRect

  // size in pixels
  def width: Int = tile.width
  def height: Int = tile.height

  def gridWidth: Int = tile.width * Tile.SquareWidth
  def gridHeight: Int = tile.height * Tile.SquareHeight

  def getPos: GridLocation = gridRect.pos

  // set pos on grid (in cases)
  def setPos(pos: GridLocation) {
    _gridRect = new GridRectangle(pos, tile.width * Tile.SquareWidth, tile.height * Tile.SquareHeight)
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


*/
