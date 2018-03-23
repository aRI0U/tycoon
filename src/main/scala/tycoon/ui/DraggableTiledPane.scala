package tycoon.ui


import tycoon.game.GridLocation
import tycoon.game.TileMap

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
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.{Color}



class DraggableTiledPane(val tm: TileMap) extends BorderPane {
  private val canvas = new Canvas
  children.add(canvas)

  canvas.width <== this.width
  canvas.height <== this.height

  private val gc = canvas.graphicsContext2D

  def clearCanvas() = {
    gc.fill = Color.LightGreen
    gc.fillRect(0, 0, canvas.width.value, canvas.height.value)
  }
  clearCanvas()

  // bounds for zooming
  private val scaleMin = 0.1
  private val scaleMax = 1.4

  // current zoom level (1.0 being default)
  private val tilesScaleX = DoubleProperty(1.0)
  private val tilesScaleY = DoubleProperty(1.0)

  // tiles size given zoom level, in pixels
  private val scaledTilesHeight = new ReadOnlyDoubleWrapper()
  private val scaledTilesWidth = new ReadOnlyDoubleWrapper()
  scaledTilesHeight <== tilesScaleY * Tile.SquareHeight
  scaledTilesWidth <== tilesScaleX * Tile.SquareWidth

  // amount scrolled left and up, in pixels
  private var xOffset = DoubleProperty(0)
  private var yOffset = DoubleProperty(0)

  // number of whole tiles shifted left and up
  private var tileXOffset = new ReadOnlyIntegerWrapper()
  private var tileYOffset = new ReadOnlyIntegerWrapper()
  tileXOffset <== xOffset / scaledTilesWidth
  tileYOffset <== yOffset / scaledTilesHeight

  // limits for the offset values given the size of the tilemap
  private var minOffsetX: Double = 0
  private var minOffsetY: Double = 0
  private var maxOffsetX: Double = 0
  private var maxOffsetY: Double = 0

  private def computeOffsetXLimits(): Unit = {
    minOffsetX = 0
    maxOffsetX = math.max(tm.width * scaledTilesWidth.value - this.width.value, 0)
  }
  private def computeOffsetYLimits(): Unit = {
    minOffsetY = 0
    maxOffsetY = math.max(tm.height * scaledTilesHeight.value - this.height.value, 0)
  }

  computeOffsetXLimits()
  computeOffsetYLimits()

  // for enabling dragging
  private var mouseAnchorX: Double = 0
  private var mouseAnchorY: Double = 0

  // for enabling camera sliding when mouse is released (smoother dragging)
  private var cameraSliding = new Timeline
  private val slidingCoef: Double = 8 // coef which determines amount of pixels to be shifted when releasing mouse
  private val slidingTime: Double = 0.5 // time during which the sliding continues after mouse is released (in seconds)
  private var deltaX: Double = 0
  private var deltaY: Double = 0

  onMousePressed = (e: MouseEvent) => {
    mouseAnchorX = e.getSceneX()
    mouseAnchorY = e.getSceneY()

    deltaX = 0
    deltaY = 0
    cameraSliding.stop()
  }

  onMouseDragged = (e: MouseEvent) => {
    deltaX = mouseAnchorX -  e.getSceneX()
    deltaY = mouseAnchorY - e.getSceneY()

    xOffset.set(xOffset.value + deltaX)
    yOffset.set(yOffset.value + deltaY)

    mouseAnchorX = e.getSceneX()
    mouseAnchorY = e.getSceneY()
  }

  onMouseReleased = _ => {
    val xBegin : Double = xOffset.value
    val xEnd : Double = math.max(math.min(xOffset.value + deltaX * slidingCoef, maxOffsetX), minOffsetX)
    val yBegin : Double = yOffset.value
    val yEnd : Double = math.max(math.min(yOffset.value + deltaY * slidingCoef, maxOffsetY), minOffsetY)

    cameraSliding = new Timeline {
      keyFrames = Seq(
        at (0.s) { xOffset -> xBegin},
        at (slidingTime.s) { xOffset -> xEnd tween Interpolator.EaseOut},
        at (0.s) { yOffset -> yBegin },
        at (slidingTime.s) { yOffset -> yEnd tween Interpolator.EaseOut}
      )
    }
    cameraSliding.play()

    requestFocus // enable KeyEvents to be caught (in onKeyPressed)
  }

  onKeyPressed = (e: KeyEvent) => {
    e.text match {
      case "a" | "A" => cameraSliding.stop()
                        tilesScaleX.set(math.max(tilesScaleX.value - 0.1, scaleMin))
                        tilesScaleY.set(math.max(tilesScaleY.value - 0.1, scaleMin))
                        computeOffsetXLimits()
                        computeOffsetYLimits()

      case "e" | "E" => cameraSliding.stop()
                        tilesScaleX.set(math.min(tilesScaleX.value + 0.1, scaleMax))
                        tilesScaleY.set(math.min(tilesScaleY.value + 0.1, scaleMax))
                        computeOffsetXLimits()
                        computeOffsetYLimits()

      case "z" | "Z" => yOffset.set(yOffset.value - 100)
      case "q" | "Q" => xOffset.set(xOffset.value - 100)
      case "s" | "S" => yOffset.set(yOffset.value + 100)
      case "d" | "D" => xOffset.set(xOffset.value + 100)
      case _ => ()
    }
  }

  // when window is resized
  this.width.onChange {
    computeOffsetXLimits()
    render()
  }
  this.height.onChange {
    computeOffsetYLimits()
    render()
  }

  def render() = {
    // ensure the offsets do not go over the limits
    if (xOffset.value <= minOffsetX) xOffset.set(minOffsetX)
    else if (xOffset.value >= maxOffsetX) xOffset.set(maxOffsetX)
    if (yOffset.value <= minOffsetY) yOffset.set(minOffsetY)
    else if (yOffset.value >= maxOffsetY) yOffset.set(maxOffsetY)

    // maximal rectangle of the tilemap that can be displayed
    val minRow: Int = Math.floor(yOffset.value / scaledTilesHeight.value).toInt //- 1
    val minCol: Int = Math.floor(xOffset.value / scaledTilesWidth.value).toInt //- 1
    val maxRow: Int = Math.min(Math.ceil((yOffset.value + canvas.height.value) / scaledTilesHeight.value).toInt, tm.height - 1)
    val maxCol: Int = Math.min(Math.ceil((xOffset.value + canvas.width.value) / scaledTilesWidth.value).toInt, tm.width - 1)

    // pre-compute some values
    val layoutShiftX = - scaledTilesWidth.value * tileXOffset.value - (xOffset.value % scaledTilesWidth.value)
    val layoutShiftY = - scaledTilesHeight.value * tileYOffset.value - (yOffset.value % scaledTilesHeight.value)

    clearCanvas()

    for {
      layer <- 0 until tm.layers
      row <- minRow to maxRow
      col <- minCol to maxCol
    } {
      tm.getTile(layer, col, row) match {
        case Some(tile) => {
          val layoutX = scaledTilesWidth.value * col + layoutShiftX
          val layoutY = scaledTilesHeight.value * row + layoutShiftY

          val width = tile.width * scaledTilesWidth.value + 1 // extra pixel for smoother overlap
          val height = tile.height * scaledTilesHeight.value + 1

          gc.drawImage(Tile.tileset, tile.sx, tile.sy, tile.sw, tile.sh, layoutX, layoutY, width, height)
        }
        case _ => ()
      }
    }
  }

  /** given an absolute position on the screen (in pixels), return the GridLocation in which it is, depending on the offset */
  def screenPxToGridLoc(x : Double, y : Double) : (Int, Int) = {
    val col : Int = Math.floor((x + xOffset.value) / scaledTilesWidth.value).toInt
    val row : Int = Math.floor((y + yOffset.value) / scaledTilesHeight.value).toInt
    (col, row)
  }
}
