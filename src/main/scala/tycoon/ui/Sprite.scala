package tycoon.ui

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.{Image, ImageView}
import javafx.scene.transform
import javafx.scene._

object Sprite {
  private val tileset = new Image("file:src/main/resources/tileset.png")

  val tile_width = 32
  val tile_height = 32

  val tile_tree = new Tile(tileset, new Rectangle2D(96, 32*2, 32, 32))
  val tile_rock = new Tile(tileset, new Rectangle2D(96 , 32*3 , 32, 32))
  val tile_house = new Tile(tileset, new Rectangle2D(0, 32 * 3, 64, 32))
  val tile_mine = new Tile(tileset, new Rectangle2D(32 * 2, 32 * 3, 32, 32))
  val tile_locomotive = new Tile(tileset, new Rectangle2D(32, 32 * 2, 32, 32))

  //straight reils
  val tile_straight_rail1 = new Tile(tileset, new Rectangle2D(0, 0 , 32, 32))
  val tile_straight_rail2 = new Tile(tileset, new Rectangle2D(0, 0 , 32, 32))
  tile_straight_rail2.getView.rotate = 90

  //Turning rails
  val tile_turning_rail = new Tile(tileset, new Rectangle2D(32 , 0 , 32, 32))
  val tile_turning_rail2 = new Tile(tileset, new Rectangle2D(32 , 0 , 32, 32))
  tile_turning_rail2.getView.rotate = 90
  val tile_turning_rail3 = new Tile(tileset, new Rectangle2D(32 , 0 , 32, 32))
  tile_turning_rail3.getView.rotate = 90
  val tile_turning_rail4 = new Tile(tileset, new Rectangle2D(32 , 0 , 32, 32))
  tile_turning_rail4.getView.rotate = 90

  //grass
  val tile_grass1 = new Tile(tileset, new Rectangle2D(0, 32, 32, 32))
  val tile_grass2= new Tile(tileset, new Rectangle2D(32, 32, 32, 32))
  val tile_grass3= new Tile(tileset, new Rectangle2D(64, 32, 32, 32))
  val tile_grass4= new Tile(tileset, new Rectangle2D(96, 32, 32, 32))
  val tile_grass5= new Tile(tileset, new Rectangle2D(96, 0, 32, 32))
  val tile_grass6= new Tile(tileset, new Rectangle2D(64, 0, 32, 32))

  //Array of grass tiles, lately choosen randomly
  val tiles_grass = Array(tile_grass1, tile_grass2,tile_grass3,tile_grass4,tile_grass5,tile_grass6)
}
