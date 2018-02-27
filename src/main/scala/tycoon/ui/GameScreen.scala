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
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer._ // Add, Remove, Reorder, Update



class GameScreen(var game : Game) extends BorderPane
{


  private val tiledPane = new DraggableTiledPane(game.tilemap, game.padding)

  
  game.entities.onChange((_, changes) => {
    for (change <- changes)
      change match {
        case Add(_, added) => ()
        case Remove(pos, removed)           => ()
        case Reorder(from, to, permutation) => ()
        case Update(pos, updated)           => ()
      }
  })

  center = new BorderPane {

    style = "-fx-background-color: lightgreen"
    center = tiledPane

  }

}
