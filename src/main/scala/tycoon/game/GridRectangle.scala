package tycoon.game



class GridRectangle(private var _pos: GridLocation, private var _cols: Int, private var _rows: Int) {

  def this(x: Int, y: Int, cols: Int, rows: Int) {
    this(new GridLocation(x, y), cols, rows)
  }

  def left = _pos.col
  def top = _pos.row
  def right = _pos.col + _cols - 1
  def bottom = _pos.row + _rows - 1

  def cols = _cols
  def rows = _rows

  def cols_= (newCols: Int) = _cols = newCols // vérifier que ca change bien right bottom et tout ; ou mettre properties
  def rows_= (newRows: Int) = _rows = newRows

  def pos: GridLocation = _pos
  def pos_= (newPos: GridLocation) = _pos = newPos

  def contains(other: GridLocation): Boolean =
    (other.col >= left && other.col <= left + cols - 1 && other.row >= top && other.row <= top + rows - 1)

  def intersects(other: GridRectangle): Boolean =
    (left <= other.right && right >= other.left && top <= other.bottom && bottom >= other.top)

  def iterate = {
    for {
      col <- left to right
      row <- top to bottom
    } yield new GridLocation(col, row)
  }
  def iterateTuple = iterate map { pos: GridLocation => (pos.col, pos.row) }
}
