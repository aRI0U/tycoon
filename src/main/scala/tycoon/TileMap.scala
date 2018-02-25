package tycoon


import tycoon.ui.Tile
import scala.collection.mutable.HashMap


class TileMap {
  val map = HashMap.empty[GridLocation, Tile]

  private var _col_min : Int = 0
  private var _col_max : Int = 0
  private var _row_min : Int = 0
  private var _row_max : Int = 0

  private var _tile_width : Int = 0
  private var _tile_height : Int = 0

  def col_min : Int = _col_min
  def col_max : Int = _col_max
  def row_min : Int = _row_min
  def row_max : Int = _row_max

  def tile_width : Int = _tile_width
  def tile_height : Int = _tile_height

  def setSize(width: Int, height: Int) : Unit = {
    _col_min = - width / 2
    _col_max  = _col_min + width - 1
    _row_min  = - height / 2
    _row_max = _row_min + height - 1
  }

  def fill(tile: Tile) : Unit = {
    map.clear()
    _tile_width = tile.width
    _tile_height = tile.height
    for {
      col <- col_min to col_max
      row <- row_min to row_max
    } map += (new GridLocation(col, row) -> new Tile(tile))
  }

  def fillBorder(tile: Tile, borderSize: Int) : Unit = {
    for {
      col <- col_min - borderSize to col_max + borderSize
      row <- row_min - borderSize to row_max + borderSize
      if (col < col_min || col > col_max || row < row_min || row > row_max)
    } map += (new GridLocation(col, row) -> new Tile(tile))
  }
}
