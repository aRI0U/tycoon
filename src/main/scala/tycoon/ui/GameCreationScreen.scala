package tycoon.ui

import tycoon.{Game, GridLocation}
import tycoon.objects.structure.Town


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
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer._ // Add, Remove, Reorder, Update


class GameCreationScreen(var game : Game) extends BorderPane
{
  game.entities.onChange((_, changes) => {
    for (change <- changes)
      change match {
        case Add(_, added) =>
          added.foreach(town => {
            tiledPane.addEntity(town)
          })
        case Remove(pos, removed)           => ()
        case Reorder(from, to, permutation) => ()
        case Update(pos, updated)           => ()
      }
  })

  private val tiledPane = new DraggableTiledPane(game.tilemap, game.padding)

  private val min_towns : Int = 2
  private val max_towns : Int = 50 // tmp

  private var nb_towns : Int = 0

  // check whether click is simple click or drag
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
        // creation of a city
        if (nb_towns < max_towns) {
          val pos : GridLocation = tiledPane.screenPxToGridLoc(e.x, e.y)

          if(game.createTown(pos)) {
            nb_towns += 1
          }
        }
      }
    }
  }

  top = new HBox {
    style = """-fx-background-color: linear-gradient(darkgreen, green, green);
               -fx-border-color: transparent transparent black transparent;
               -fx-border-width: 1;"""
    alignment = Pos.Center

    children = Seq(
      new Text {
        text = "Click to place " + min_towns + " up to " + max_towns + " cities"
        margin = Insets(10)
      },
      new Text {
        text = "→"
        margin = Insets(10)
      },
      new TextField {
        promptText = "Choose a name"
        margin = Insets(10)
      },
      new Text {
        text = "→"
        margin = Insets(10)
      },
      new Button {
        text = "Play"
        margin = Insets(10)
      }
    )
    onMouseClicked = (e: MouseEvent) => { requestFocus() }
  }
}
