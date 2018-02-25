package tycoon.ui

import tycoon.{Game, GridLocation}


import scalafx.Includes._
import scalafx.scene.Scene

import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Stops, LinearGradient}
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, Insets, Rectangle2D}
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scala.collection.mutable.ListBuffer

import scalafx.scene.input.MouseEvent

import scala.collection.mutable.HashMap
import scalafx.scene.control.{TextField, Button}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, HBox}



class GameCreationScreen(var game : Game) extends BorderPane
{
  //game.entities.onChange((source, changes) => { println(source, changes) })

  center = new BorderPane {
    var tiledPane = new DraggableTiledPane(game.tilemap, game.padding)

    style = "-fx-background-color: lightgreen"
    center = tiledPane

    // creation of a City
    onMouseClicked = (e: MouseEvent) => {
      requestFocus()
      val pos : GridLocation = tiledPane.pixelToCase(e.getSceneX(), e.getSceneY())
      game.create_town(pos)
    }
  }

  top = new HBox {
    style = """-fx-background-color: linear-gradient(darkgreen, green, green);
               -fx-border-color: transparent transparent black transparent;
               -fx-border-width: 1;"""

    alignment = Pos.Center
    children = Seq(
      new Text {
        text = "Click to place 1 up to 5 cities"
        margin = Insets(10)
      },
      new Text {
        text = "=>"
        margin = Insets(10)
      },
      new TextField {
        promptText = "Enter your name"
        margin = Insets(10)
      },
      new Text {
        text = "=>"
        margin = Insets(10)
      },
      new Button {
        text = "Play (SPACE)"
        margin = Insets(10)
      }
    )
  }
}
