package tycoon.ui

import tycoon.{Game, GridLocation}
import tycoon.objects.game._
import tycoon.ui._

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

import scalafx.scene.control.{TextField, Button}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, HBox}

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

  private var mouse_anchor_x : Double = 0
  private var mouse_anchor_y : Double = 0

  center = new BorderPane {

    style = "-fx-background-color: lightgreen"
    center = tiledPane

    onMousePressed = (e: MouseEvent) => {
      requestFocus()

      mouse_anchor_x = e.x
      mouse_anchor_y = e.y
    }

    onMouseReleased = (e: MouseEvent) => {
      if (e.x == mouse_anchor_x && e.y == mouse_anchor_y) {
        // Select entity and then shows information on side bar

      }
    }
  }
  //should have entyties transmission ... Like the Class Player should be created in Game creation screen and transmited.
  //same question for the cities ect..
  val player_class = new Player

  private var player_money = player_class.money
  val player_name = player_class.name
  //val city_number =  nb_towns.get()

  top = new HBox {
    style = """-fx-background-color: linear-gradient(burlywood, burlywood, brown);
               -fx-border-color: transparent transparent black transparent;
               -fx-border-width: 1;"""
    alignment = Pos.TOP_LEFT
/*
    private val name_field = new TextField {
      promptText = "Choose a name"
      margin = Insets(10)
    }
*/
    children = Seq(
      new Text {
        text = "player : " + player_name + "   "
        margin = Insets(10)
      },
      new Text {
        text =  "Capital :"
        margin = Insets(10)
      },
      new Text {
        text =  player_class.money + " $    "
        if (player_class.money >0) {
          fill = Green
        }
        else {fill = Red}
        margin = Insets(10)
      },
      new Text {
        text = "Acitons :"
        margin = Insets(10)
      },
      /*
      name_field,
      new Text {
        text = "→"
        margin = Insets(10)
      },
      */
      new Button {
        text = "Rail construction"
        margin = Insets(10)

        onMouseClicked = (e: MouseEvent) => {
            //Open new game mode about mine construction
        }
      },

      new Button {
        text = "Mine Construction"
        margin = Insets(10)

        onMouseClicked = (e: MouseEvent) => {
          //Open new game mode about mine construction
        }
      }
    )
    onMouseClicked = (e: MouseEvent) => { requestFocus() }
  }
}
