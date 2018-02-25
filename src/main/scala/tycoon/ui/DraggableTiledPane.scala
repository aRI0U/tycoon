package tycoon.ui


import tycoon.GridLocation
import tycoon.TileMap

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

import scala.collection.mutable.{HashMap}


class DraggableTiledPane(tm: TileMap, paddingTiles: Int)
extends BorderPane {
  // amount scrolled left and up, in pixels
  private var x_offset = DoubleProperty(0)
  private var y_offset = DoubleProperty(0)

  // number of whole tiles shifted left and up
  private var tile_x_offset = new ReadOnlyIntegerWrapper()
  private var tile_y_offset = new ReadOnlyIntegerWrapper()
  tile_x_offset <== x_offset / tm.tile_width
  tile_y_offset <== y_offset / tm.tile_height

  // for enabling dragging
  private var mouse_anchor_x : Double = 0
  private var mouse_anchor_y : Double = 0

  // amount scrollable around the tilemap, in pixels
  private var padding_around_x : Double = paddingTiles * tm.tile_width
  private var padding_around_y : Double = paddingTiles * tm.tile_height

  // limits for the offsets according to the size of the tilemap and the padding_arounds
  // min/max are there to ensure that the pane cannot be dragged if the map is smaller than the window
  private var min_offset_x : Double = 0
  private var min_offset_y : Double = 0
  private var max_offset_x : Double = 0
  private var max_offset_y : Double = 0

  compute_offset_limits_x()
  compute_offset_limits_y()

  private def compute_offset_limits_x() : Unit = {
    min_offset_x = math.min((this.width.value / 2) - ((tm.col_max + 1) * tm.tile_width) - padding_around_x, 0)
    max_offset_x = math.max((- this.width.value / 2) + (- tm.col_min * tm.tile_width) + padding_around_x, 0)
  }
  private def compute_offset_limits_y() : Unit = {
    min_offset_y = math.min((this.height.value / 2) - ((tm.row_max + 1) * tm.tile_height) - padding_around_y, 0)
    max_offset_y = math.max((- this.height.value / 2) + (- tm.row_min * tm.tile_height)  + padding_around_y, 0)
  }

  // for enabling dragging
  onMousePressed = (e : MouseEvent) => {
    mouse_anchor_x = e.getSceneX()
    mouse_anchor_y = e.getSceneY()
  }

  // update offsets and pane layout when dragging
  onMouseDragged = (e : MouseEvent) => {
    // compute the new offsets
    val delta_x : Double = e.getSceneX() - mouse_anchor_x
    val delta_y : Double = e.getSceneY() - mouse_anchor_y
    x_offset.set(x_offset.get() + delta_x)
    y_offset.set(y_offset.get() + delta_y)
    mouse_anchor_x = e.getSceneX()
    mouse_anchor_y = e.getSceneY()

    // ensure the offsets do not go over the limits
    if (x_offset.get() <= min_offset_x) x_offset.set(min_offset_x)
    else if (x_offset.get() >= max_offset_x) x_offset.set(max_offset_x)
    if (y_offset.get() <= min_offset_y) y_offset.set(min_offset_y)
    else if (y_offset.get() >= max_offset_y) y_offset.set(max_offset_y)

    layoutChildren()
  }

  // recompute layout and offsets limits when window is resized
  this.width.onChange {
    compute_offset_limits_x()
    layoutChildren()
  }
  this.height.onChange {
    compute_offset_limits_y()
    layoutChildren()
  }

  def layoutChildren() = {
    // maximal rectangle of the tilemap that can be displayed
    val min_col : Int = - Math.ceil(((this.width.value / 2) + x_offset.get()) / tm.tile_width).toInt
    val max_col : Int = Math.ceil(((this.width.value / 2) - x_offset.get()) / tm.tile_width).toInt - 1
    val min_row : Int = - Math.ceil(((this.height.value / 2) + y_offset.get()) / tm.tile_height).toInt
    val max_row : Int = Math.ceil(((this.height.value / 2) - y_offset.get()) / tm.tile_height).toInt - 1

    // move the tilemap according to the offset
    for ((pos, tile) <- tm.map) {
      if (pos.inRangeInclusive(min_col, max_col, min_row, max_row))
      {
        // if tile can be displayed, compute its position
        val layout_x : Double = (this.width.value / 2) + tm.tile_width * (pos.column + tile_x_offset.get()) + x_offset.get() % tm.tile_width
        val layout_y : Double = (this.height.value / 2) + tm.tile_height * (pos.row + tile_y_offset.get()) + y_offset.get() % tm.tile_height

        tile.setPos(layout_x, layout_y)

        if (!tile.displayed) {
          // if tile was not displayed yet, add it to the scene
          tile.displayed = true
          children.add(tile.getView)
        }
      } else if (!pos.inRangeInclusive(min_col, max_col, min_row, max_row) && tile.displayed) {
        // if tile was displayed but is not in range anymore, remove it from the scene
        tile.displayed = false
        children.remove(tile.getView)
      }
    }
  }

  // given a pixel on the screen, return the GridLocation in which it is, depending on the offset
  def pixelToCase(x : Double, y : Double) : GridLocation = {
    val col : Int = Math.floor((x - (this.width.value / 2) - x_offset.get()) / tm.tile_width).toInt
    val row : Int = Math.floor((y - (this.height.value / 2) - y_offset.get()) / tm.tile_height).toInt

    new GridLocation(col, row)
  }
}
