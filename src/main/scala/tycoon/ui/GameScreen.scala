package tycoon.ui

import tycoon.{Game, GridLocation}
import tycoon.Player
import tycoon.objects.railway.BasicRail

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.beans.property._
import scalafx.application.Platform
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Stops, LinearGradient}
import scalafx.scene.layout.{BorderPane, VBox, StackPane, Pane}
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, HPos, VPos, Insets, Rectangle2D, Orientation}
import scalafx.geometry.Orientation._
import scalafx.scene.control.{Button, Separator, ButtonType, Alert}
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
import scala.collection.mutable.ListBuffer

class GameScreen(var game : Game) extends BorderPane
{
  private var onRailClick = new Runnable { def run() {} }
  def setOnRailClick(r : Runnable) = {
    onRailClick = r
  }
  private var onMineClick = new Runnable { def run() {} }
  def setOnMineClick(r : Runnable) = {
    onMineClick = r
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


  private val txt_tmp : String = "salut"
  private var buy_train = BooleanProperty(false)

  def maybeClickEntityAt(pos: GridLocation) {
    for (ent <- game.entities) {
      if (ent.gridContains(pos)) {
          ent match {
            case rail : BasicRail => {
              if (buy_train.get()){
                println("you just clicked on a rail")
                if (buy_train.get()){
                  if (game.createTrain(rail)) {
                    //money changes
                  return

                  //train création
                  }
                }
              }
            }
            case _ => {
              println("b")
              bindPrintData(ent.printData)
              println("c")
              return
            }
        }
        // else {
        //   bindPrintData(ent.printData)
        //   return
        // }
      }
    }
  }
  def bindPrintData(data: ListBuffer[(String, StringProperty)]) {
    menuPane.center = new VBox {
      for (elt <- data) {
        val item = new Text {
          text <== StringProperty(elt._1 + ": ").concat(elt._2)
          println ("a")
          margin = Insets(5)
        }

        children.add(item)
        println ("a")
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

  private val menuPane = new BorderPane {
    //style = """-fx-background-color: linear-gradient(burlywood, burlywood, brown);
    style = """-fx-border-color: transparent black transparent transparent;
               -fx-border-width: 1;
               -fx-background-color: #DDD;"""

    top = new VBox {
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
            onMineClick.run()
            //Open new game mode about mine construction
          }
        },
        new Button {
          text = "Buy a train"
          margin = Insets(10)

          onMouseClicked = (e: MouseEvent) => {
            //Open new game mode about train construction
            if (buy_train.get()) {
              buy_train.set(false)
            }
            else buy_train.set(true)
          }
        },
        new Text {
          text <== when (buy_train) choose ("Now select a rail") otherwise ("")
          margin = Insets(5)
        },
        new Separator {
          orientation = Orientation.Horizontal
          style = """-fx-border-style: solid;
                     -fx-background-color: black;
                     -fx-border-color: black;"""
        }
      )
    }

    bottom = new VBox {
      alignment = Pos.BottomCenter
      children = Seq(new Button {
        text = "Quit Game"
        margin = Insets(10)

        onMouseClicked = (e: MouseEvent) => {
          val alert = new Alert(Alert.AlertType.Warning) {
            title = "Careful!"
            headerText = "If you leave now, your progress will not be saved."
            contentText = "Are you sure you want to leave?"
            buttonTypes = Seq(ButtonType.Cancel, ButtonType.Yes)
          }

          val result = alert.showAndWait()
          result match {
            case Some(ButtonType.Yes) => Platform.exit()
            case _                   => ()
          }
        }
      })
    }

    //onMouseClicked = (e: MouseEvent) => { requestFocus() }
  }
}
