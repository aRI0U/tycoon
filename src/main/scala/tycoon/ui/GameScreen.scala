package tycoon.ui

import tycoon.{Game, GridLocation}
import tycoon.Player
import tycoon.objects.railway._
import tycoon.objects.structure._

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
import scalafx.scene.control.{Button, Separator, ButtonType, Alert, TextArea, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{MouseEvent, KeyEvent}

import scala.collection.mutable.{HashMap, HashSet}
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer._ // Add, Remove, Reorder, Update

import scalafx.scene.control.{TextField, Button}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, HBox, VBox, GridPane, Priority}

import scalafx.beans.property.{StringProperty, IntegerProperty, BooleanProperty}
import scalafx.beans.binding.Bindings
import scala.collection.mutable.ListBuffer





class GameScreen(var game : Game) extends BorderPane
{
  stylesheets += "style/gamescreen.css"
  id = "body"

  private var buyingRailsMode : Boolean = false
  private var nb_rails = IntegerProperty(0)
  private var total_cost_rails = IntegerProperty(0)

  private var buyingMinesMode : Boolean = false
  private var nb_mines = IntegerProperty(0)
  private var total_cost_mines = IntegerProperty(0)

  private var tripCreationMode : Boolean = false
  private var first_town_selected = BooleanProperty(false)
  private var first_town : Town = _

  private def init_buyingMines () {
    buyingMinesMode = true
    buyingRailsMode = false
    tripCreationMode = false
    nb_mines.set(0)
    total_cost_mines.set(0)

    menuPane.center = new VBox {

      children = Seq(
        new Text {
          text <== StringProperty("Cost: $").concat(total_cost_mines.asString)
          fill <== when (total_cost_mines < game.playerMoney) choose Green otherwise Red
          margin = Insets(10)
        },
        new Button {
          text = "Remove"
          margin = Insets(10)
          onMouseClicked = _ => {
            if (nb_mines.get() > 0) {
              nb_mines.set(nb_mines.get()-1)
              game.removeAllMines()
              game.playerMoney.set(game.playerMoney.get() + game.mine_price)
              total_cost_mines.set(total_cost_mines.get() - game.mine_price)
            }
          }
        },
        new Button {
          text = "Exit mine construction" ; margin = Insets(10)
          onMouseClicked = (e: MouseEvent) => {
            buyingMinesMode = false
            menuPane.center = actionsPane
          }
        },
        new Separator { orientation = Orientation.Horizontal ; styleClass += "sep" }
      )
    }
  }
  private def init_buyingRails () {
    buyingRailsMode = true
    buyingMinesMode = false
    tripCreationMode = false
    nb_rails.set(0)
    total_cost_rails.set(0)

    menuPane.center = new VBox {

      children = Seq(
        new Text {
          text <== StringProperty("Cost: $").concat(total_cost_rails.asString)
          fill <== when (total_cost_rails < game.playerMoney) choose Green otherwise Red
          margin = Insets(10)
        },
        new Button {
          text = "Remove"
          margin = Insets(10)
          onMouseClicked = _ => {
            if (nb_rails.get() > 0) {
              nb_rails.set(nb_rails.get()-1)
              game.removeAllRails()
              game.playerMoney.set(game.playerMoney.get() + game.rail_price)
              total_cost_rails.set(total_cost_rails.get() - game.rail_price)
            }
          }
        },
        new Button {
          text = "Exit rail construction" ; margin = Insets(10)
          onMouseClicked = (e: MouseEvent) => {
            buyingRailsMode = false
            menuPane.center = actionsPane
          }
        },
        new Separator { orientation = Orientation.Horizontal ; styleClass += "sep" }
      )
    }
  }


  private def init_tripCreation () {
    tripCreationMode = true
    buyingRailsMode = false
    buyingMinesMode = false

    first_town_selected.set(false)

    menuPane.center = new VBox {

      children = Seq(
        new Text {
          text <== when (first_town_selected) choose "Select destination town" otherwise "Select depart town"
          margin = Insets(10)
        },
        new Button {
          text = "Exit trip creation" ; margin = Insets(10)
          onMouseClicked = (e: MouseEvent) => {
            tripCreationMode = false
            menuPane.center = actionsPane
          }
        },
        new Separator { orientation = Orientation.Horizontal ; styleClass += "sep" }
      )
    }
  }



  private var mouse_anchor_x : Double = 0
  private var mouse_anchor_y : Double = 0


  def init () : Unit = {
    center = gamePane
    left = menuPane
    gamePane.center = game.tiledPane // update
  }

  private var buy_train = BooleanProperty(false)

  def maybeClickEntityAt(pos: GridLocation) {
    for (ent <- game.entities) {
      if (ent.gridContains(pos)) {
        ent match {
          case town : Town => {
            if (buy_train.get()) {
              println("new train in that town")
              if (game.createTrain(town)) return
            }
            if (tripCreationMode) {
              if (first_town_selected.get()) {
                if (first_town != town) {
                  first_town.getTrain() match {
                    case Some(train) => {
                      game.createRoute(first_town, town, train)
                      println("create trip from " + first_town.name + " to " + town.name)
                    }
                    case None => println("no train no gain")
                  }
                  tripCreationMode = false
                  menuPane.center = actionsPane
                }
              } else {
                first_town_selected.set(true)
                first_town = town
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

  private val actionsPane = new VBox {
    children = Seq(
      new Button {
        text = "Rail construction" ; margin = Insets(10)
        onMouseClicked = _ => init_buyingRails()
      },
      new Button {
        text = "Mine Construction" ; margin = Insets(10)
        onMouseClicked = _ => init_buyingMines()
      },
      new Button {
        text = "Buy a train" ; margin = Insets(10)

        onMouseClicked = _ => {
          //Open new game mode about train construction
          if (buy_train.get()) {
            buy_train.set(false)
          }
          else buy_train.set(true)
        }
      },
      new Button {
        text = "Create a train trip" ; margin = Insets(10)

        onMouseClicked = _ => init_tripCreation()
      },
      new Separator { orientation = Orientation.Horizontal ; styleClass += "sep" }
    )
  }



  private val gamePane = new BorderPane {


    onMousePressed = (e: MouseEvent) => {
      requestFocus()

      mouse_anchor_x = e.x
      mouse_anchor_y = e.y
    }

    onMouseReleased = (e: MouseEvent) => {
      if (e.x == mouse_anchor_x && e.y == mouse_anchor_y) {

        val pos : GridLocation = game.tiledPane.screenPxToGridLoc(e.x, e.y)

        if (buyingMinesMode) {
          if (game.mine_price <= game.playerMoney.get() ){ //creation of a new mine
            if (game.createMine(pos)) {
              nb_mines.set(nb_mines.get() + 1)
              total_cost_mines.set((total_cost_mines.get() + game.mine_price))
              game.playerMoney.set(game.playerMoney.get() - game.mine_price)
            }
          }
        }
        else if (buyingRailsMode) {
          if (game.rail_price <= game.playerMoney.get() ){ //creation of a new mine
            if (game.createRail(pos)) {
              nb_rails.set(nb_rails.get() + 1)
              total_cost_rails.set((total_cost_rails.get() + game.rail_price))
              game.playerMoney.set(game.playerMoney.get() - game.rail_price)
            }
          }
        }
        else {
          maybeClickEntityAt(pos)
        }
      }
    }
  }

  private val menuPane = new BorderPane {
    id = "menu"

    bottom = new VBox {
      children = Seq(
        new Separator { orientation = Orientation.Horizontal ; styleClass += "sep" },
        new Text {
          text = "Time: TODO"
          margin = Insets(5)
        },
        new Text {
          text <== StringProperty("Player: ").concat(game.playerName)
          margin = Insets(5)
        },
        new Text {
          text <== StringProperty("Balance: $").concat(game.playerMoney.asString)
          fill <== when (game.playerMoney > 0) choose Green otherwise Red
          margin = Insets(5)
        },
        new Separator { orientation = Orientation.Horizontal ; styleClass += "sep" },
        new HelpAndQuitButtons
      )
    }


    top = actionsPane

    bottom = new HelpAndQuitButtons

    onMouseClicked = (e: MouseEvent) => { requestFocus() }
  }
}
