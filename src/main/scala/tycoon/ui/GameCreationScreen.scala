package tycoon.ui

import tycoon.game._
import tycoon.objects.structure.Town
// import tycoon..Gridlocation


import scalafx.Includes._
import scalafx.scene.Scene

import scalafx.beans.property.{StringProperty, IntegerProperty}
import scalafx.beans.binding.Bindings

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
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer._ // Add, Remove, Reorder, Update


class GameCreationScreen(var game : Game) extends BorderPane
{
  private var onValidate = new Runnable { def run() {} }

  def setOnValidate(r : Runnable) = {
    onValidate = r
  }

  private val min_towns : Int = 0
  private val max_towns : Int = 15

  private var nb_towns = IntegerProperty(0)
  private var nb_towns_str = new StringProperty
  nb_towns_str <== nb_towns.asString

  // check whether click is simple click or drag
  private var mouse_anchor_x : Double = 0
  private var mouse_anchor_y : Double = 0

  def init () : Unit = {
    center = gamePane
    top = menuPane
    gamePane.center = game.tiledPane // update
  }

  private val gamePane = new BorderPane {

    style = "-fx-background-color: lightgreen"

    onMousePressed = (e: MouseEvent) => {
      requestFocus

      mouse_anchor_x = e.x
      mouse_anchor_y = e.y
    }

    onMouseReleased = (e: MouseEvent) => {
      if (e.x == mouse_anchor_x && e.y == mouse_anchor_y) {
        // creation of a city
        if (nb_towns.value < max_towns) {
          val pos = game.tiledPane.screenPxToGridLoc(e.x, e.y)
          if(game.createTown(pos)) {
            nb_towns.set(nb_towns.value + 1)
          }
        }
      }
    }
  }


  private val menuPane = new HBox {
    style = """-fx-background-color: linear-gradient(darkgreen, green, green);
               -fx-border-color: transparent transparent black transparent;
               -fx-border-width: 1;"""
    alignment = Pos.Center

    private val name_field = new TextField {
      promptText = "Choose a player name"
      margin = Insets(10)
    }

    children = Seq(
      new Text {
        text = "Click to place " + min_towns + " up to " + max_towns + " cities"
        margin = Insets(10)
      },
      new Text {
        text <== nb_towns_str + "/" + max_towns
        fill <== when (nb_towns >= min_towns) choose Blue otherwise Red
        margin = Insets(10)
      },
      new Text {
        text = "→"
        margin = Insets(10)
      },
      name_field,
      new Text {
        text = "→"
        margin = Insets(10)
      },
      new Button {
        text = "Play"
        margin = Insets(10)

        onMouseClicked = (e: MouseEvent) => {
          if (nb_towns.value >= min_towns) {
            game.playerName = name_field.text.value
            onValidate.run()
          }
        }
      },
      new Text {
        text = "|"
        margin = Insets(10)
      },
      new Button {
        text = "Reset"
        margin = Insets(10)

        onMouseClicked = (e: MouseEvent) => {
          nb_towns.set(0)
          name_field.text = ""
          game.removeAllTowns()
        }
      }
    )
    onMouseClicked = (e: MouseEvent) => { requestFocus() }
  }
}
