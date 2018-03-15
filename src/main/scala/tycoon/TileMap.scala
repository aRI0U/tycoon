package tycoon

import tycoon.ui.Tile
import scala.util.Random
import scalafx.geometry.Rectangle2D


class TileMap (_width: Int, _height: Int) {
  private val _map = Array.ofDim[Tile](width, height)

  private var _tiles_width: Double = 0
  private var _tiles_height: Double = 0

  def map: Array[Array[Tile]] = _map

  def tiles_width: Double = _tiles_width
  def tiles_height: Double = _tiles_height
  def tiles_width_= (new_tiles_width: Double) = _tiles_width = new_tiles_width
  def tiles_height_= (new_tiles_height: Double) = _tiles_height = new_tiles_height

  def width: Int = _width
  def height: Int = _height

  def gridContains(rect: GridRectangle) =
    (rect.left >= 0 && rect.top >= 0 && rect.right <= width - 1 && rect.bottom <= height - 1)

  /* fill map randomly using tiles */
  def fill(tiles: Array[Tile]) : Unit = {
    if (tiles.length >= 1) {
      val r = scala.util.Random
      for {
        row <- 0 to height - 1
        col <- 0 to width - 1
      } _map(row)(col) = new Tile(tiles(r.nextInt(tiles.length)))
    }
  }
}
