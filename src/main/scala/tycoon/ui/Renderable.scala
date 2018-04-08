package tycoon.ui

import tycoon.game.GridLocation
import tycoon.game.GridRectangle
import tycoon.game.PrintableData

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.ImageView
import scala.collection.mutable.ListBuffer
import scalafx.beans.property.StringProperty

abstract class Renderable(private var pos: GridLocation) {
  private var _tile: Tile = Tile.default
  private var _gridRect: GridRectangle = new GridRectangle(pos, 1, 1)
  private val _printData = new ListBuffer[PrintableData]
  //private val _printData: ListBuffer[(String, StringProperty)] = ListBuffer()
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
  def gridPos_= (newGridPos: GridLocation) = _gridRect.pos = newGridPos

  def gridIntersects(other: Renderable): Boolean = gridRect.intersects(other.gridRect)
  def gridContains(pos: GridLocation): Boolean = gridRect.contains(pos)


  // size in pixels ;; nécessaire ?
  def width: Int = tile.width
  def height: Int = tile.height

  def gridWidth: Int = tile.width * Tile.SquareWidth
  def gridHeight: Int = tile.height * Tile.SquareHeight
}
