package tycoon.ui

import tycoon.{Game, GridLocation}
import tycoon.Player

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.beans.property._

import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Stops, LinearGradient}
import scalafx.scene.layout.{BorderPane, VBox, StackPane, Pane}
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, HPos, VPos, Insets, Rectangle2D, Orientation}
import scalafx.geometry.Orientation._
import scalafx.scene.control.{Button, Separator}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{MouseEvent, KeyEvent}

import scala.collection.mutable.{HashMap, HashSet}
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer._ // Add, Remove, Reorder, Update

import scalafx.scene.control.{TextField, Button}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, HBox, VBox}

import scalafx.beans.property.{StringProperty, IntegerProperty}
import scalafx.beans.binding.Bindings

class GameScreen(var game : Game) extends BorderPane
{
  private var onRailClick = new Runnable { def run() {} }
  def setOnRailClick(r : Runnable) = {
    onRailClick = r
  }

  // private val tiledPane = new DraggableTiledPane(game.tilemap, game.padding)


  game.entities.onChange((_, changes) => { // also used for game creation
    for (change <- changes)
      change match {
        case Add(_, added) =>
          added.foreach(town => game.tiledPane.addEntity(town))
        case Remove(_, removed) =>
          removed.foreach(town => game.tiledPane.removeEntity(town))
        case Reorder(from, to, permutation) => ()
        case Update(pos, updated)           => ()
      }
  })

  private var mouse_anchor_x : Double = 0
  private var mouse_anchor_y : Double = 0


  def init () : Unit = {
    center = gamePane
    left = menuPane
    gamePane.center = game.tiledPane // update
  }

  def maybeClickEntityAt(pos: GridLocation) {
    for (ent <- game.entities) {
      if (ent.gridContains(pos)) {
        println("entity clicked!!")
        // TODO add trait clickable to some entities
        // which will implement a method containing what to display in the side bar
        // or better do onclick method in this trait and take as parameter pane in which to print
        return
      }
    }
  }


  private val gamePane = new BorderPane {

    style = "-fx-background-color: lightgreen"

    onMousePressed = (e: MouseEvent) => {
      requestFocus()

      mouse_anchor_x = e.x
      mouse_anchor_y = e.y
    }

    onMouseReleased = (e: MouseEvent) => {
      if (e.x == mouse_anchor_x && e.y == mouse_anchor_y) {
        // Select entity and then shows information on side bar
        val pos : GridLocation = game.tiledPane.screenPxToGridLoc(e.x, e.y)

        maybeClickEntityAt(pos)
      }
    }
  }
  //should have entyties transmission ... Like the Class Player should be created in Game creation screen and transmited.
  //same question for the cities ect..

  //val city_number =  nb_towns.get()

  private val menuPane = new VBox {
    //style = """-fx-background-color: linear-gradient(burlywood, burlywood, brown);
    style = """-fx-border-color: transparent transparent black transparent;
               -fx-border-width: 1;
               -fx-background-image: url("wood_pattern.png");
               -fx-background-repeat: repeat;"""

    children = Seq(
      new Text {
        text <== StringProperty("Player: ").concat(game.playerName)
        margin = Insets(5)
      },
      new Text {
        text <== StringProperty("Balance: $").concat(game.playerMoney.asString)
        fill <== when (game.playerMoney > 0) choose Green otherwise Red
        margin = Insets(5)
      },
      new Separator {
        orientation = Orientation.Horizontal
        style = """-fx-border-style: solid;
                   -fx-background-color: black;
                   -fx-border-color: black;"""
      },
      new Text {
        text = "Actions : TODO"
        margin = Insets(10)
      },
      new Button {
        text = "Rail construction"
        margin = Insets(10)

        onMouseClicked = (e: MouseEvent) => {
          onRailClick.run()
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
