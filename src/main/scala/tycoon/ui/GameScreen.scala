package tycoon.ui


import tycoon.Game

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.beans.property._

import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Stops, LinearGradient}
import scalafx.scene.layout.{BorderPane, VBox, StackPane, Pane}
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, HPos, VPos, Insets, Rectangle2D}
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{MouseEvent, KeyEvent}

import scala.collection.mutable.{HashMap, HashSet}



class GameScreen(var game : Game) extends BorderPane
{
  final val tileset = new Image("file:src/main/resources/tileset.png")
  var tiledPane = new PanningTiledPane(32, 32, tileset)

  style = "-fx-background-color: lightgreen"
  center = tiledPane


}

/* TODO

appeler layoutChildren() quand on resize la fenêtre
ajouter le double click (ou espace) qui ramène au centre
séparer le pane en plusieurs classes et mettre le tileset qq part dans Game
appeler layoutChildren() quand on clique sur NewGame() de sorte que ça marche
ajouter un déplacement avec les touches haut bas gauche droite (just incrémenter/décrémenter offset)
généraliser le Gridlocation aux autres classes
class Tile extends Renderable qui prend en param un ImageView et qui gère son viewport etc

tryhard: un système de vitesse
tryhard: mettre tt le calcul dans un autre thread

*/



class PanningTiledPane(tile_width : Int, tile_height : Int, tileset : Image)
extends BorderPane {
  // amount scrolled left and up, in pixels
  private var x_offset = DoubleProperty(0)
  private var y_offset = DoubleProperty(0)

  // number of whole tiles shifted left and up
  private var tile_x_offset = new ReadOnlyIntegerWrapper()
  private var tile_y_offset = new ReadOnlyIntegerWrapper()
  tile_x_offset <== x_offset / tile_width
  tile_y_offset <== y_offset / tile_height

  // for enabling dragging
  private var mouse_anchor_x : Double = 0
  private var mouse_anchor_y : Double = 0

  private final var tilemap : HashMap[GridLocation, Tile]
    = HashMap.empty[GridLocation, Tile]

  // temporary for test
  // map size in tiles
  val map_width : Int = 200
  val map_height : Int = 200
  // min & max, cols & row
  val map_min_col : Int = - map_width / 2
  val map_max_col : Int = map_min_col + map_width - 1
  val map_min_row : Int = - map_height / 2
  val map_max_row : Int = map_min_row + map_height - 1

  for (col <- map_min_col to map_max_col) {
    for (row <- map_min_row to map_max_row) {
      val pos = new GridLocation(col, row)
      val tile = new Tile(tile_width, tile_height, tileset, 32 * (((col%15)+15)%15), 32 * (((row%15)+15)%15))
      tilemap += (pos -> tile)
    }
  }

  val padding_arround : Double = 100

  val min_offset_x = new ReadOnlyDoubleWrapper()
  val max_offset_x = new ReadOnlyDoubleWrapper()
  val min_offset_y = new ReadOnlyDoubleWrapper()
  val max_offset_y = new ReadOnlyDoubleWrapper()
  min_offset_x <== (this.width / 2) - ((map_max_col + 1) * tile_width) - padding_arround
  max_offset_x <== (- this.width / 2) + (- map_min_col * tile_width) + padding_arround
  min_offset_y <== (this.height / 2) - ((map_max_row + 1) * tile_height) - padding_arround
  max_offset_y <== (- this.height / 2) + (- map_min_row * tile_height)  + padding_arround

  // enable panning on pane (just update offsets when dragging)
  onMousePressed = (e : MouseEvent) => {
    mouse_anchor_x = e.getSceneX();
    mouse_anchor_y = e.getSceneY();
  }

  onMouseDragged = (e : MouseEvent) => {
    println(min_offset_x, x_offset.get(), max_offset_x, (this.width.value / 2) - ((map_max_col + 1) * tile_width))
    val delta_x : Double = e.getSceneX() - mouse_anchor_x
    val delta_y : Double = e.getSceneY() - mouse_anchor_y
    x_offset.set(x_offset.get() + delta_x)
    y_offset.set(y_offset.get() + delta_y)

    if (x_offset.get() <= min_offset_x.get()) x_offset.set(min_offset_x.get())
    else if (x_offset.get() >= max_offset_x.get()) x_offset.set(max_offset_x.get())
    if (y_offset.get() <= min_offset_y.get()) y_offset.set(min_offset_y.get())
    else if (y_offset.get() >= max_offset_y.get()) y_offset.set(max_offset_y.get())

    mouse_anchor_x = e.getSceneX()
    mouse_anchor_y = e.getSceneY()
    layoutChildren()
  }

  // recompute layout when window is resized
  this.width.onChange { layoutChildren() }
  this.height.onChange { layoutChildren() }

  def layoutChildren() = {
    // maximal box of the tilemap that is to be displayed
    val min_col : Int = - Math.ceil(((this.width.value / 2) + x_offset.get()) / tile_width).toInt
    val max_col : Int = Math.ceil(((this.width.value / 2) - x_offset.get()) / tile_width).toInt - 1
    val min_row : Int = - Math.ceil(((this.height.value / 2) + y_offset.get()) / tile_height).toInt
    val max_row : Int = Math.ceil(((this.height.value / 2) - y_offset.get()) / tile_height).toInt - 1

    for ((pos, tile) <- tilemap) {
      if (pos.inRangeInclusive(min_col, max_col, min_row, max_row))
      {
        val layout_x : Double = (this.width.value / 2) + tile_width * (pos.column + tile_x_offset.get()) + x_offset.get() % tile_width
        val layout_y : Double = (this.height.value / 2) + tile_height * (pos.row + tile_y_offset.get()) + y_offset.get() % tile_height
        tile.getView.layoutX = layout_x
        tile.getView.layoutY = layout_y
        if (!tile.displayed) {
          tile.displayed = true
          children.add(tile.getView)
        }
      } else if (!pos.inRangeInclusive(min_col, max_col, min_row, max_row) && tile.displayed) {
        tile.displayed = false
        children.remove(tile.getView)
      }
    }
  }

  // move the entire pane (sur double click ?)
  def moveTo(tileX : Int, tileY : Int, pos : Pos) {
    var xPos : Int = 0
    pos.hpos match {
      case HPos.Left => xPos = 0
      case HPos.Center => xPos = 1
      case HPos.Right => xPos = 2
      case _ => xPos = 0
    }
    var yPos : Int = 0
    pos.vpos match {
      case VPos.Top => yPos = 0
      case VPos.Center => yPos = 1
      case VPos.Bottom => yPos = 2
      case _ => yPos = 0
    }

    x_offset.set(tileX * tile_width + (this.width.value - tile_width) * xPos / 2);
    y_offset.set(tileY * tile_height + (this.height.value - tile_height) * yPos / 2);
  }



  private class GridLocation(val column : Int, val row : Int) {
    def inRangeInclusive(minCol : Int, maxCol : Int, minRow : Int, maxRow : Int) : Boolean = {
      column >= minCol && column <= maxCol && row >= minRow && row <= maxRow
    }
  }
}

class Tile(width : Double, height : Double, tileset : Image, tileset_x : Double, tileset_y : Double) {
  private var view : ImageView = new ImageView(tileset)
  var displayed : Boolean = false
  view.viewport = new Rectangle2D(tileset_x, tileset_y, width, height)

  def getView : ImageView = view
}
