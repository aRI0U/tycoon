package tycoon.ui


import tycoon.Game
import tycoon.DraggableTiledPane

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
  var tiledPane = new DraggableTiledPane(game.tilemap, game.map_min_col, game.map_max_col, game.map_min_row, game.map_max_row)

  style = "-fx-background-color: lightgreen"
  center = tiledPane

  onMouseClicked = (e: MouseEvent) => {
    tiledPane.pixelToCase(e.getSceneX(), e.getSceneY())
  }

}
