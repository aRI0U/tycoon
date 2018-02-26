package tycoon.objects

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.{Image, ImageView}

import tycoon.ui.Tile


object Sprite {
  private val tileset = new Image("file:src/main/resources/tileset.png")

  val tile_grass = new Tile(tileset, new Rectangle2D(0, 0, 32, 32))
  val tile_tree = new Tile(tileset, new Rectangle2D(0, 32, 32, 32))
  val tile_rock = new Tile(tileset, new Rectangle2D(32 * 6, 32 * 11, 32, 32))
  val tile_house = new Tile(tileset, new Rectangle2D(32 * 6, 32 * 12, 64, 32))
}
