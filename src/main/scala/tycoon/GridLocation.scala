package tycoon

class GridLocation(val column : Int, val row : Int) {

  def get_x = column
  def get_y = row

  def inRangeInclusive(minCol : Int, maxCol : Int, minRow : Int, maxRow : Int) : Boolean = {
    column >= minCol && column <= maxCol && row >= minRow && row <= maxRow
  }

}
