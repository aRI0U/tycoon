package tycoon.game

import tycoon.ui.Tile
import scala.util.Random
import scalafx.geometry.Rectangle2D


class TileMap (_width: Int, _height: Int) {
  private val _map = Array.ofDim[Tile](width, height)

  private var _tilesWidth: Double = 0
  private var _tilesHeight: Double = 0

  def map: Array[Array[Tile]] = _map

  def tilesWidth: Double = _tilesWidth
  def tilesHeight: Double = _tilesHeight
  def tilesWidth_= (newTilesWidth: Double) = _tilesWidth = newTilesWidth
  def tilesHeight_= (newTilesHeight: Double) = _tilesHeight = newTilesHeight

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
