package tycoon


import tycoon.ui.Tile


// location (0, 0) is at the top-left corner
class GridLocation(private var _col: Int, private var _row: Int) {
  //private var _x: Double = _col * Tile.square_width
  //private var _y: Double = _row * Tile.square_height

  // absolute position in squares (which size is determined by Tile.square_width and Tile.square_height)
  def col: Int = _col
  def row: Int = _row

  // absolute position in pixels
  //def x: Double = _x
  //def y: Double = _y

  /*def setPxPos(x: Double, y: Double) {
    _x = x
    _y = y
    _col = Math.floor(x / Tile.square_width).toInt
    _row = Math.floor(y / Tile.square_height).toInt
  }*/

  def setGridPos(col: Int, row: Int) {
    _col = col
    _row = row
    /*_x = _col * Tile.square_width
    _y = _row * Tile.square_height*/
  }
}
