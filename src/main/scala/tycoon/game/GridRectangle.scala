package tycoon.game



class GridRectangle(topLeftCorner: GridLocation, _cols: Int, _rows: Int) {
  def left = topLeftCorner.col
  def top = topLeftCorner.row
  def right = topLeftCorner.col + _cols - 1
  def bottom = topLeftCorner.row + _rows - 1

  def cols = _cols
  def rows = _rows

  def pos: GridLocation = new GridLocation(left, top)

  def contains(pos: GridLocation): Boolean =
    (pos.col >= left && pos.col <= left + cols - 1 && pos.row >= top && pos.row <= top + rows - 1)

  def intersects(other: GridRectangle): Boolean =
    (left <= other.right && right >= other.left && top <= other.bottom && bottom >= other.top)

  def iterate = {
    for {
      col <- left to right
      row <- top to bottom
    } yield (col, row)
  }
}
