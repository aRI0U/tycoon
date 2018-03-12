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

import scalafx.animation.{Timeline, Interpolator}
import scala.collection.mutable.{HashMap, ListBuffer}


/*TODO

re-compute offset correctly when zooming/unzooming so that the center of the current screen doesnt move

start with an offset at the center of the map (not top-left corner)

*/


class DraggableTiledPane(val tm: TileMap) extends BorderPane {

  private val scale_min = 0.4
  private val scale_max = 1.4
  private val scale_x = DoubleProperty(1.0)
  private val scale_y = DoubleProperty(1.0)
  private val scaled_tiles_height = DoubleProperty(0)
  private val scaled_tiles_width = DoubleProperty(0)

  scaled_tiles_height <== scale_y * tm.tiles_height
  scaled_tiles_width <== scale_x * tm.tiles_width


  // amount scrolled left and up, in pixels
  private var x_offset = DoubleProperty(0)
  private var y_offset = DoubleProperty(0)

  // number of whole tiles shifted left and up
  private var tile_x_offset = new ReadOnlyIntegerWrapper()
  private var tile_y_offset = new ReadOnlyIntegerWrapper()
  tile_x_offset <== x_offset / scaled_tiles_width
  tile_y_offset <== y_offset / scaled_tiles_height

  // limits for the offsets according to the size of the tilemap and the padding_arounds
  // min/max are there to ensure that the pane cannot be dragged if the map is smaller than the window
  private var min_offset_x : Double = 0
  private var min_offset_y : Double = 0
  private var max_offset_x : Double = 0
  private var max_offset_y : Double = 0

  compute_offset_x_limits()
  compute_offset_y_limits()

  private def compute_offset_x_limits() : Unit = {
    min_offset_x = 0
    max_offset_x = math.max(tm.width * scaled_tiles_width.get() - this.width.get(), 0)
  }
  private def compute_offset_y_limits() : Unit = {
    min_offset_y = 0
    max_offset_y = math.max(tm.height * scaled_tiles_height.get() - this.height.get(), 0)
  }

  // for enabling dragging
  private var mouse_anchor_x : Double = 0
  private var mouse_anchor_y : Double = 0

  // camera sliding when mouse is released for fluid dragging
  private var cameraSliding = new Timeline
  private var delta_x : Double = 0
  private var delta_y : Double = 0

  onMousePressed = (e : MouseEvent) => {
    // for enabling dragging
    mouse_anchor_x = e.getSceneX()
    mouse_anchor_y = e.getSceneY()

    // for enabling camera sliding
    delta_x = 0
    delta_y = 0
    cameraSliding.stop()
  }

  // update offsets when dragging
  onMouseDragged = (e : MouseEvent) => {
    delta_x = mouse_anchor_x -  e.getSceneX()
    delta_y = mouse_anchor_y - e.getSceneY()

    x_offset.set(x_offset.get() + delta_x)
    y_offset.set(y_offset.get() + delta_y)

    mouse_anchor_x = e.getSceneX()
    mouse_anchor_y = e.getSceneY()
  }

  // enable soft sliding
  onMouseReleased = _ => {
    val coef : Double = 8
    val x_begin : Double = x_offset.get()
    val x_end : Double = math.max(math.min(x_offset.get() + delta_x * coef, max_offset_x), min_offset_x)
    val y_begin : Double = y_offset.get()
    val y_end : Double = math.max(math.min(y_offset.get() + delta_y * coef, max_offset_y), min_offset_y)

    cameraSliding = new Timeline {
      keyFrames = Seq(
        at (0.s) { x_offset -> x_begin},
        at (0.5.s) { x_offset -> x_end tween Interpolator.EaseOut},
        at (0.s) { y_offset -> y_begin },
        at (0.5.s) { y_offset -> y_end tween Interpolator.EaseOut}
      )
    }
    cameraSliding.play()

    requestFocus // so that onKeyPressed works
  }

  onKeyPressed = (e: KeyEvent) => {
    e.text match {
      case "a" | "A" => cameraSliding.stop()
                        scale_x.set(math.max(scale_x.get() - 0.1, scale_min))
                        scale_y.set(math.max(scale_y.get() - 0.1, scale_min))
                        compute_offset_x_limits()
                        compute_offset_y_limits()
                        layoutTilemap()
                        layoutEntities()

      case "e" | "E" => cameraSliding.stop()
                        scale_x.set(math.min(scale_x.get() + 0.1, scale_max))
                        scale_y.set(math.min(scale_y.get() + 0.1, scale_max))
                        compute_offset_x_limits()
                        compute_offset_y_limits()
                        layoutTilemap()
                        layoutEntities()
                        
      // ZQSD => <20% CPU (mouse dragging is causing the overheating)
      case "z" | "Z" => y_offset.set(y_offset.get() - 100)
      case "q" | "Q" => x_offset.set(x_offset.get() - 100)
      case "s" | "S" => y_offset.set(y_offset.get() + 100)
      case "d" | "D" => x_offset.set(x_offset.get() + 100)
      case _ => ()
    }
  }

  x_offset.onChange {
    layoutTilemap()
    layoutEntities()
  }
  y_offset.onChange {
    layoutTilemap()
    layoutEntities()
  }

  // recompute layout and offsets limits when window is resized
  this.width.onChange {
    compute_offset_x_limits()
    layoutTilemap()
    //layoutEntities()
  }
  this.height.onChange {
    compute_offset_y_limits()
    layoutTilemap()
    //layoutEntities()
  }


  // tiles other than the tilemap (entities)
  // these may be placed everywhere and have different sizes
  // also their position may change without any drag but that can be handled in Tile or via a Movable trait
  private var entities = new ListBuffer[Renderable]()


  def addEntity(e: Renderable) = {
    entities += e
    layoutEntities()
  }
  def removeEntity(e: Renderable) = {
    entities -= e
    if (e.displayed) {
      e.displayed = false
      children.remove(e.getView)
    }
    layoutEntities()
  }

  private var prev_min_row : Int = Math.floor(y_offset.get() / tm.tiles_height).toInt // its tile_y_offset
  private var prev_min_col : Int = Math.floor(x_offset.get() / tm.tiles_width).toInt
  private var prev_max_col : Int = Math.ceil((x_offset.get() + this.width.value) / tm.tiles_width).toInt
  private var prev_max_row : Int = Math.ceil((y_offset.get() + this.height.value) / tm.tiles_height).toInt

  def layoutTilemap() = {
    // ensure the offsets do not go over the limits
    if (x_offset.get() <= min_offset_x) x_offset.set(min_offset_x)
    else if (x_offset.get() >= max_offset_x) x_offset.set(max_offset_x)
    if (y_offset.get() <= min_offset_y) y_offset.set(min_offset_y)
    else if (y_offset.get() >= max_offset_y) y_offset.set(max_offset_y)

    // maximal rectangle of the tilemap that can be displayed
    val min_row : Int = Math.floor(y_offset.get() / scaled_tiles_height.get()).toInt - 1
    val min_col : Int = Math.floor(x_offset.get() / scaled_tiles_width.get()).toInt - 1
    val max_row : Int = Math.ceil((y_offset.get() + this.height.value) / scaled_tiles_height.get()).toInt
    val max_col : Int = Math.ceil((x_offset.get() + this.width.value) / scaled_tiles_width.get()).toInt

    /* to optimize:
      x_offset.get() % scaled_tiles_width.get() moves outta the loop
      avoid the if in the for and the inside if by performing more loops but accurate ones
      and mb just increment layout given delta_offset to avoid calculations
    */

    // move the tilemap according to the offset
    for {
      row <- math.min(min_row, prev_min_row) to math.max(max_row, prev_max_row)
      col <- math.min(min_col, prev_min_col) to math.max(max_col, prev_max_col)
      if (row < tm.height && col < tm.width && row >= 0 && col >= 0)
    } {
      val tile = tm.map(row)(col)

      if (row >= min_row && row <= max_row && col >= min_col && col <= max_col) // tile shall be displayed
      {

        // offset due to scale
        // 0.5 (16) -> -8
        // 1.5 (48) -> +8
        // zoom in 16 steps -> 1/16 = 0.0625

        // if tile can be displayed, compute its position
        val layout_x : Double = scaled_tiles_width.get() * (col - tile_x_offset.get()) - (x_offset.get() % scaled_tiles_width.get())
        val layout_y : Double = scaled_tiles_height.get() * (row - tile_y_offset.get()) - (y_offset.get() % scaled_tiles_height.get())

        tile.setLayout(layout_x, layout_y)

        //tile.getView.scaleX.set(scale_x.get())
        //tile.getView.scaleY.set(scale_y.get())

        tile.getView.setFitWidth(scaled_tiles_width.get())
        tile.getView.setFitHeight(scaled_tiles_height.get())

        if (!tile.displayed) {
          // if tile was not displayed yet, add it to the scene
          tile.displayed = true
          children.add(tile.getView)
        }
      } else if (tile.displayed) { // ELSE SHOULD SUFFICE ; if tile is not in screen range anymore
        tile.displayed = false
        children.remove(tile.getView)
      }
    }

    prev_min_row = min_row
    prev_min_col = min_col
    prev_max_row = max_row
    prev_max_col = max_col
  }

  def layoutEntities() = {
    // move the entities according to the offset
    for (e <- entities) {
      val layout_x : Double = scaled_tiles_width.get() * (e.gridLoc.column - tile_x_offset.get()) - (x_offset.get() % scaled_tiles_width.get())
      val layout_y : Double = scaled_tiles_height.get() * (e.gridLoc.row - tile_y_offset.get()) - (y_offset.get() % scaled_tiles_height.get())

      if ((layout_x + e.width > 0 && layout_x < this.width.value)
        && (layout_y + e.height > 0 && layout_y < this.height.value))
      {
        // if entity is within scene, display it
        e.setLayout(layout_x, layout_y)

        e.getView.setFitWidth(e.width * scale_x.get())
        e.getView.setFitHeight(e.height * scale_y.get())

        if (!e.displayed) {
          // if entity was not displayed yet, add it to the scene
          e.displayed = true

          children.add(e.getView)
        }
        else { // opti
          children.remove(e.getView)
          children.add(e.getView)
        }
      } else if (e.displayed) {
        // if entity was displayed but is not in range anymore, remove it from the scene
        e.displayed = false
        children.remove(e.getView)
      }
    }
  }

  // given a position on the screen (in pixels), return the GridLocation in which it is, depending on the offset
  def screenPxToGridLoc(x : Double, y : Double) : (Int, Int) = {
    val col : Int = Math.floor((x + x_offset.get()) / scaled_tiles_width.get()).toInt
    val row : Int = Math.floor((y + y_offset.get()) / scaled_tiles_height.get()).toInt
    println(col, row)
    (col, row)
  }

  def moveToCenter() : Unit = {
    x_offset.set(0)
    y_offset.set(0)
  }
}
