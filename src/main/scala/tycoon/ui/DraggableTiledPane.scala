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
  val canvas = new Canvas
  canvas.width <== this.width
  canvas.height <== this.height
  children.add(canvas)

  val gc = canvas.graphicsContext2D
  gc.fill = Color.LightGreen
  gc.fillRect(0, 0, canvas.width.value, canvas.height.value)

  val tileset = new Image("file:src/main/resources/tileset.png") // tmprily here

  private val scaleMin = 0.1
  private val scaleMax = 1.4
  private val tilesScaleX = DoubleProperty(1.0)
  private val tilesScaleY = DoubleProperty(1.0)
  private val scaledTilesHeight = DoubleProperty(0)
  private val scaledTilesWidth = DoubleProperty(0)

  scaledTilesHeight <== tilesScaleY * tm.tilesHeight
  scaledTilesWidth <== tilesScaleX * tm.tilesWidth


  // amount scrolled left and up, in pixels
  private var xOffset = DoubleProperty(0)
  private var yOffset = DoubleProperty(0)

  // number of whole tiles shifted left and up
  private var tileXOffset = new ReadOnlyIntegerWrapper()
  private var tileYOffset = new ReadOnlyIntegerWrapper()
  tileXOffset <== xOffset / scaledTilesWidth
  tileYOffset <== yOffset / scaledTilesHeight

  // limits for the offsets according to the size of the tilemap and the padding_arounds
  // min/max are there to ensure that the pane cannot be dragged if the map is smaller than the window
  private var minOffsetX: Double = 0
  private var minOffsetY: Double = 0
  private var maxOffsetX: Double = 0
  private var maxOffsetY: Double = 0

  computeOffsetXLimits()
  computeOffsetYLimits()

  private def computeOffsetXLimits(): Unit = {
    minOffsetX = 0
    maxOffsetX = math.max(tm.width * scaledTilesWidth.get() - this.width.get(), 0)
  }
  private def computeOffsetYLimits(): Unit = {
    minOffsetY = 0
    maxOffsetY = math.max(tm.height * scaledTilesHeight.get() - this.height.get(), 0)
  }

  // for enabling dragging
  private var mouseAnchorX: Double = 0
  private var mouseAnchorY: Double = 0

  // camera sliding when mouse is released for fluid dragging
  private var cameraSliding = new Timeline
  private var deltaX: Double = 0
  private var deltaY: Double = 0

  onMousePressed = (e: MouseEvent) => {
    // for enabling dragging
    mouseAnchorX = e.getSceneX()
    mouseAnchorY = e.getSceneY()

    // for enabling camera sliding
    deltaX = 0
    deltaY = 0
    cameraSliding.stop()
  }

  // update offsets when dragging
  onMouseDragged = (e: MouseEvent) => {
    deltaX = mouseAnchorX -  e.getSceneX()
    deltaY = mouseAnchorY - e.getSceneY()

    xOffset.set(xOffset.get() + deltaX)
    yOffset.set(yOffset.get() + deltaY)

    mouseAnchorX = e.getSceneX()
    mouseAnchorY = e.getSceneY()
  }

  // enable soft sliding
  onMouseReleased = _ => {
    val coef : Double = 8
    val xBegin : Double = xOffset.get()
    val xEnd : Double = math.max(math.min(xOffset.get() + deltaX * coef, maxOffsetX), minOffsetX)
    val yBegin : Double = yOffset.get()
    val yEnd : Double = math.max(math.min(yOffset.get() + deltaY * coef, maxOffsetY), minOffsetY)

    cameraSliding = new Timeline {
      keyFrames = Seq(
        at (0.s) { xOffset -> xBegin},
        at (0.5.s) { xOffset -> xEnd tween Interpolator.EaseOut},
        at (0.s) { yOffset -> yBegin },
        at (0.5.s) { yOffset -> yEnd tween Interpolator.EaseOut}
      )
    }
    cameraSliding.play()

    requestFocus // so that onKeyPressed works
  }

  onKeyPressed = (e: KeyEvent) => {
    e.text match {
      case "a" | "A" => cameraSliding.stop()
                        tilesScaleX.set(math.max(tilesScaleX.get() - 0.1, scaleMin))
                        tilesScaleY.set(math.max(tilesScaleY.get() - 0.1, scaleMin))
                        computeOffsetXLimits()
                        computeOffsetYLimits()
                        //layoutTilemap()
                        //layoutEntities()

      case "e" | "E" => cameraSliding.stop()
                        tilesScaleX.set(math.min(tilesScaleX.get() + 0.1, scaleMax))
                        tilesScaleY.set(math.min(tilesScaleY.get() + 0.1, scaleMax))
                        computeOffsetXLimits()
                        computeOffsetYLimits()
                        //layoutTilemap()
                        //layoutEntities()

      // ZQSD => <20% CPU (mouse dragging is causing the overheating)
      case "z" | "Z" => yOffset.set(yOffset.get() - 100)
      case "q" | "Q" => xOffset.set(xOffset.get() - 100)
      case "s" | "S" => yOffset.set(yOffset.get() + 100)
      case "d" | "D" => xOffset.set(xOffset.get() + 100)
      case _ => ()
    }
  }

/*  xOffset.onChange {
    layoutTilemap()
    layoutEntities()
  }
  yOffset.onChange {
    layoutTilemap()
    layoutEntities()
  }
*/
  // recompute layout and offsets limits when window is resized
  this.width.onChange {
    computeOffsetXLimits()
    render()
    //layoutEntities()
  }
  this.height.onChange {
    computeOffsetYLimits()
    render()
    //layoutEntities()
  }


  // tiles other than the tilemap (entities)
  // these may be placed everywhere and have different sizes
  // also their position may change without any drag but that can be handled in Tile or via a Movable trait
  private var entities = new ListBuffer[Entity]()


  def addEntity(e: Entity) = {
    entities += e
    //layoutEntities()
  }
  def removeEntity(e: Entity) = {
    entities -= e
    if (e.visible) {
      e.visible = false
      children.remove(e.getView)
    }
    //layoutEntities()
  }

  private var prevMinRow: Int = 0
  private var prevMinCol: Int = 0
  private var prevMaxCol: Int = 0
  private var prevMaxRow: Int = 0

  def render() = {
    // ensure the offsets do not go over the limits
    if (xOffset.get() <= minOffsetX) xOffset.set(minOffsetX)
    else if (xOffset.get() >= maxOffsetX) xOffset.set(maxOffsetX)
    if (yOffset.get() <= minOffsetY) yOffset.set(minOffsetY)
    else if (yOffset.get() >= maxOffsetY) yOffset.set(maxOffsetY)

    // maximal rectangle of the tilemap that can be displayed
    val minRow: Int = Math.floor(yOffset.get() / scaledTilesHeight.get()).toInt //- 1
    val minCol: Int = Math.floor(xOffset.get() / scaledTilesWidth.get()).toInt //- 1
    val maxRow: Int = Math.min(Math.ceil((yOffset.get() + canvas.height.value) / scaledTilesHeight.get()).toInt, tm.height - 1)
    val maxCol: Int = Math.min(Math.ceil((xOffset.get() + canvas.width.value) / scaledTilesWidth.get()).toInt, tm.width - 1)

    // pre-compute some values
    val layoutShiftX = - scaledTilesWidth.value * tileXOffset.value - (xOffset.value % scaledTilesWidth.value)
    val layoutShiftY = - scaledTilesHeight.value * tileYOffset.value - (yOffset.value % scaledTilesHeight.value)

    /*
    iterate through the tiles that are displayed right now
    */
    gc.fillRect(0, 0, canvas.width.value, canvas.height.value)

    for {
      row <- minRow to maxRow
      col <- minCol to maxCol
    } {
      val tile = tm.map(row)(col)

      val layoutX : Double = scaledTilesWidth.value * col + layoutShiftX
      val layoutY : Double = scaledTilesHeight.value * row + layoutShiftY

      gc.drawImage(tile.tileset, tile.getView.viewport.value.minX, tile.getView.viewport.value.minY, tile.width, tile.height, layoutX, layoutY, scaledTilesWidth.value, scaledTilesHeight.value)


      /*if (!tile.inScene) {
        // if tile was not displayed yet, add it to the scene
        tile.inScene = true
        children.add(tile.getView)
      } */
    }

    prevMinRow = minRow
    prevMinCol = minCol
    prevMaxRow = maxRow
    prevMaxCol = maxCol


    // move the entities according to the offset
    for (e <- entities) {
      val layoutX : Double = scaledTilesWidth.get() * (e.getPos.col - tileXOffset.get()) - (xOffset.get() % scaledTilesWidth.get())
      val layoutY : Double = scaledTilesHeight.get() * (e.getPos.row - tileYOffset.get()) - (yOffset.get() % scaledTilesHeight.get())

      if ((layoutX + e.width > 0 && layoutX < this.width.value)
        && (layoutY + e.height > 0 && layoutY < this.height.value))
      {
        // if entity is within scene, display it
        e.setLayout(layoutX, layoutY)

        e.getView.setFitWidth(e.width * tilesScaleX.get())
        e.getView.setFitHeight(e.height * tilesScaleY.get())

        if (e.visible) {
          if (!e.inScene) {
            // if entity was not displayed yet but should be, add it to the scene
            children.add(e.getView)
            e.inScene = true
          }
          else { // opti -> pop entity at the top
            children.remove(e.getView)
            children.add(e.getView)
          }
        }
        else {
          if (e.inScene) {
            children.remove(e.getView)
            e.inScene = false
          }
        }
      }
      else if (e.inScene) {
        // if entity was displayed but is not in range anymore, remove it from the scene
        children.remove(e.getView)
        e.inScene = false
      }
    }
  }

  // given a position on the screen (in pixels), return the GridLocation in which it is, depending on the offset
  def screenPxToGridLoc(x : Double, y : Double) : (Int, Int) = {
    val col : Int = Math.floor((x + xOffset.get()) / scaledTilesWidth.get()).toInt
    val row : Int = Math.floor((y + yOffset.get()) / scaledTilesHeight.get()).toInt
    (col, row)
  }

  def moveToCenter() : Unit = {
    xOffset.set(0)
    yOffset.set(0)
  }
}
