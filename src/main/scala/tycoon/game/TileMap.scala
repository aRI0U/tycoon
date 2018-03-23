package tycoon.game

import tycoon.ui.Tile
import scala.util.Random
import scalafx.geometry.Rectangle2D


class TileMap (val width: Int, val height: Int, val layers: Int = 2) {
  private val map = Array.fill[Option[Tile]](layers, width, height)(None)

  def getTile(layer: Int, col: Int, row: Int) = map(layer)(col)(row)
  def addTile(layer: Int, col: Int, row: Int, tile: Tile) = map(layer)(col)(row) = Some(tile)

  def gridContains(rect: GridRectangle) =
    (rect.left >= 0 && rect.top >= 0 && rect.right <= width - 1 && rect.bottom <= height - 1)

  /* randomly fill layer 0 of map using tiles */
  def fillBackground(tiles: Array[Tile]) : Unit = {
    if (tiles.length >= 1) {
      val r = scala.util.Random
      for {
        row <- 0 to height - 1
        col <- 0 to width - 1
      } map(0)(col)(row) = Some(tiles(r.nextInt(tiles.length)))
    }
  }
}
