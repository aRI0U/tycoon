package tycoon.ui


import tycoon.Game

import scalafx.Includes._
import scalafx.scene.Scene

import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Stops, LinearGradient}
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, Insets, Rectangle2D}
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}

import scalafx.scene.input.MouseEvent

import scala.collection.mutable.HashMap



class GameScreen(var game : Game) extends BorderPane
{
  val tile_size = 32


  val map_min_x = -5
  val map_max_x = 5
  val map_min_y = -5
  val map_max_y = 5


  val tile_map : HashMap[(Int, Int), Int] = HashMap.empty[(Int, Int), Int]
  val r = scala.util.Random

  for (x <- map_min_x to map_max_x) {
    for (y <- map_min_y to map_max_y) {
      tile_map((x, y)) = r.nextInt(400)
    }
  }
  final val tileset = new Image("file:src/main/resources/tileset.png")


  style = "-fx-background-color: lightgreen"
  center = new BorderPane {
    // alignment = Pos.CENTER

    for (x <- map_min_x to map_max_x) {
      for (y <- map_min_y to map_max_y) {
        children.add(new Tile(x, y, tile_map((x, y)), tileset))
      }
    }

  }




}




class Tile(val x_pos : Int, val y_pos : Int, val tile_type : Int, val tileset : Image)
  extends ImageView
{
  val tile_size = 32
  val center_x = 320
  val center_y = 320

  image = tileset
  x = center_x + x_pos * tile_size
  y = center_y + y_pos * tile_size

  set_tile(tile_type)

  def set_tile(tile_type : Int) {
    var x_offset = 32 * (tile_type / 20)
    var y_offset = 32 * (tile_type % 20)
    viewport = new Rectangle2D(x_offset, y_offset, tile_size, tile_size)
  }

  onMouseDragged = (e: MouseEvent) => {
    x = e.getSceneX()
    y = e.getSceneY()
  }

}
