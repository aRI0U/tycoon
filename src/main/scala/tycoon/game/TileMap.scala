package tycoon.game

import tycoon.ui.Tile
import scala.util.Random
import scalafx.geometry.Rectangle2D
import scala.collection.mutable.ListBuffer
import tycoon.ui.Renderable



class TileMap (val width: Int, val height: Int) {
  private val backgroundLayer = Array.fill[Option[Tile]](width, height)(None)
  private val entities = new ListBuffer[Renderable]

  def getBackgroundTile(col: Int, row: Int) = backgroundLayer(col)(row)
  def addBackgroundTile(col: Int, row: Int, tile: Tile) = backgroundLayer(col)(row) = Some(tile)

  def addEntity(e: Renderable) = entities += e
  def removeEntity(e: Renderable) = entities -= e
  def getEntities: ListBuffer[Renderable] = entities.clone()

  def gridContains(rect: GridRectangle) =
    (rect.left >= 0 && rect.top >= 0 && rect.right <= width - 1 && rect.bottom <= height - 1)

  /* randomly fill background layer of map using tiles */
  def fillBackground(tiles: Array[Tile]) : Unit = {
    if (tiles.length >= 1) {
      val r = scala.util.Random
      for {
        row <- 0 to height - 1
        col <- 0 to width - 1
      } backgroundLayer(col)(row) = Some(tiles(r.nextInt(tiles.length)))
    }
  }
}
