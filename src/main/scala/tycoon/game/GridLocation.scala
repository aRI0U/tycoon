package tycoon.game


import tycoon.ui.Tile


// location (0, 0) is at the top-left corner
class GridLocation(private var _col: Int, private var _row: Int) {
  //private var _x: Double = _col * Tile.SquareWidth
  //private var _y: Double = _row * Tile.SquareHeight

  // absolute position in squares (which size is determined by Tile.SquareWidth and Tile.SquareHeight)
  def col: Int = _col
  def row: Int = _row

  def this(pos: GridLocation) = this(pos.col, pos.row)

  def top = new GridLocation(col, row - 1)
  def right = new GridLocation(col + 1, row)
  def bottom = new GridLocation(col, row + 1)
  def left = new GridLocation(col - 1, row)

  var pourcentageWidth: Double = 0
  var pourcentageHeight: Double = 0

  def eq(other: GridLocation): Boolean = (col == other.col && row == other.row)
  def clone(): GridLocation = new GridLocation(pos.col, pos.row)
  // absolute position in pixels
  //def x: Double = _x
  //def y: Double = _y

  /*def setPxPos(x: Double, y: Double) {
    _x = x
    _y = y
    _col = Math.floor(x / Tile.SquareWidth).toInt
    _row = Math.floor(y / Tile.SquareHeight).toInt
  }*/

  def setGridPos(col: Int, row: Int) {
    _col = col
    _row = row
    /*_x = _col * Tile.SquareWidth
    _y = _row * Tile.SquareHeight*/
  }
}
