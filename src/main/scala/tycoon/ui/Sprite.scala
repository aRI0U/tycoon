package tycoon.ui

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.{Image, ImageView}
import javafx.scene.transform
import javafx.scene._

//http://designwoop.com/2012/03/20-free-subtle-textures-for-backgrounds/

object Sprite {
  private val tileset = new Image("file:src/main/resources/3.png")

  val tile_width = 32
  val tile_height = 32

  // def this(angle: Double) = this(new transformf.Rotate(angle))
  //
  // imageView.setRotate(imageView.getRotate() + 90)

  val tile_grass1 = new Tile(tileset, new Rectangle2D(0, 32, 32, 32))
  val tile_grass2= new Tile(tileset, new Rectangle2D(32, 32, 32, 32))
  val tile_grass3= new Tile(tileset, new Rectangle2D(64, 32, 32, 32))
  val tile_tree = new Tile(tileset, new Rectangle2D(32, 32, 32, 32))
  val tile_rock = new Tile(tileset, new Rectangle2D(32*2 , 32*3 , 32, 32))
  val tile_straight_rail1 = new Tile(tileset, new Rectangle2D(0, 0 , 32, 32))
  val tile_straight_rail2 = new Tile(tileset, new Rectangle2D(0, 0 , 32, 32))
  tile_straight_rail2.getView.rotate = 90
  val tile_turning_rail = new Tile(tileset, new Rectangle2D(32 , 0 , 32, 32))
  val tile_house = new Tile(tileset, new Rectangle2D(0, 32 * 3, 64, 32))
  val tile_mine = new Tile(tileset, new Rectangle2D(32 * 2, 32 * 3, 32, 32))

  //Array of grass tiles, lately choosen randomly
  // val tiles_rail = Array(tile_straight_rail1, tile_turning_rail)
  val tiles_grass = Array(tile_grass1, tile_grass2,tile_grass3)

/*
  val tile_grass = new Tile(tileset, new Rectangle2D(0, 0, 32, 32))
  val tile_tree = new Tile(tileset, new Rectangle2D(0, 32, 32, 32))
  val tile_rock = new Tile(tileset, new Rectangle2D(32 * 6, 32 * 11, 32, 32))
  val tile_house = new Tile(tileset, new Rectangle2D(32 * 6, 32 * 12, 64, 32))
*/
}
