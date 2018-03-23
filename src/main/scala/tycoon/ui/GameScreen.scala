package tycoon.ui

import tycoon.game._
import tycoon.objects.vehicle._
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
import scalafx.scene.control.{Button, Separator, ButtonType, Alert, TextArea, Label, Slider}
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





class GameScreen(val game : Game) extends BorderPane
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
  private var firstStructureSelected = BooleanProperty(false)
  private var firstStructure : Structure = _

  private var buyingTrainMode : Boolean = false

  private def init_buyingMines () {
    buyingMinesMode = true
    buyingRailsMode = false
    tripCreationMode = false
    buyingTrainMode = false
    nb_mines.set(0)
    total_cost_mines.set(0)

    menuPane.top = new VBox {

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
            if (nb_mines.value > 0) {
              nb_mines.set(nb_mines.value-1)
              game.removeAllMines()
              game.playerMoney.set(game.playerMoney.value + game.mine_price)
              total_cost_mines.set(total_cost_mines.value - game.mine_price)
            }
          }
        },
        new Button {
          text = "Exit mine construction" ; margin = Insets(10)
          onMouseClicked = (e: MouseEvent) => {
            buyingMinesMode = false
            menuPane.top = actionsPane
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
    buyingTrainMode = false
    nb_rails.set(0)
    total_cost_rails.set(0)

    menuPane.top = new VBox {

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
            if (nb_rails.value > 0) {
              nb_rails.set(nb_rails.value-1)
              game.removeLastRails()
              game.playerMoney.set(game.playerMoney.value + game.rail_price)
              total_cost_rails.set(total_cost_rails.value - game.rail_price)
            }
          }
        },
        new Button {
          text = "Exit rail construction" ; margin = Insets(10)
          onMouseClicked = (e: MouseEvent) => {
            buyingRailsMode = false
            menuPane.top = actionsPane
          }
        },
        new Separator { orientation = Orientation.Horizontal ; styleClass += "sep" }
      )
    }
  }

  private def init_buyingTrains () {
    buyingRailsMode = false
    buyingMinesMode = false
    tripCreationMode = false
    buyingTrainMode = true

    menuPane.top = new VBox {

      children = Seq(
        new Text {
          text = "Select a town to add a train in it (costs 260)"
          margin = Insets(10)
        },
        /*new Text {
          text = "Number of passenger carriages (costs 20 per carriage):"
          margin = Insets(5)
        },*/
        new Button {
          text = "Exit train buying" ; margin = Insets(10)
          onMouseClicked = (e: MouseEvent) => {
            buyingTrainMode = false
            menuPane.top = actionsPane
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
    buyingTrainMode = false

    firstStructureSelected.set(false)

    menuPane.top = new VBox {

      children = Seq(
        new Text {
          text <== when (firstStructureSelected) choose "Select destination town" otherwise "Select depart town"
          margin = Insets(10)
        },
        new Button {
          text = "Exit trip creation" ; margin = Insets(10)
          onMouseClicked = (e: MouseEvent) => {
            tripCreationMode = false
            menuPane.top = actionsPane
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

  def maybeClickRenderableAt(pos: GridLocation): Unit = {
    game.map.getContentAt(pos) match {
      case Some(entity) => {
        entity match {
          case train : Train => {
            println("tycoon > ui > GameScreen.scala > maybeClickRenderableAt: " + train.carriages_list)
          }
          case structure : Structure => {
            structure match {
              case town : Town => {
              if (buyingTrainMode) {
                println("tycoon > ui > GameScreen.scala > maybeClickRenderableAt: new train in " + town.name + " town")
                if (game.createTrain(town)) return
              }}
              case _ => ()
            }
            if (tripCreationMode) {
              if (firstStructureSelected.value) {
                if (firstStructure != structure) {
                  firstStructure.getTrain() match {
                    case Some(train) => {
                      try {
                        game.createRoute(firstStructure, structure, train)
                        println("tycoon > ui > GameScreen.scala > maybeClickRenderableAt: create trip from " + firstStructure.name + " to " + structure.name)
                      }
                      catch {
                        case e: IllegalStateException => println("tycoon > ui > GameScreen.scala > maybeClickRenderableAt: trains cannot fly!")
                      }
                    }
                    case None => println("tycoon > ui > GameScreen.scala > maybeClickRenderableAt: no train no gain")
                  }
                  tripCreationMode = false
                  menuPane.top = actionsPane
                }
              } else {
                firstStructureSelected.set(true)
                firstStructure = structure
              }
            }
          }
          case r : Rail => { }
          case _ => {  }
        }
        bindPrintData(entity.printData)
      }
      case None => println("tycoon > ui > GameScreen.scala > maybeClickRenderableAt: nothing in map at this position")
    }
  }

  def bindPrintData(data: ListBuffer[(String, StringProperty)]) {
    menuPane.center = new VBox {
      for (elt <- data) {
        val item = new Text {
          text <== StringProperty(elt._1 + ": ").concat(elt._2)
          margin = Insets(5)
        }
        children.add(item)
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
        text = "Farm Construction" ; margin = Insets(10)
        onMouseClicked = _ => {}//init_buyingFarmes()
      },
      new Button {
        text = "Factory Construction" ; margin = Insets(10)
        onMouseClicked = _ => {}//init_buyingFactories()
      },
      new Button {
        text = "Buy a train" ; margin = Insets(10)
        onMouseClicked = _ => init_buyingTrains()
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

        val pos = new GridLocation( game.tiledPane.screenPxToGridLoc(e.x, e.y)._1, game.tiledPane.screenPxToGridLoc(e.x, e.y)._2) // TMP

        if (buyingMinesMode) {
          if (game.mine_price <= game.playerMoney.value ){ //creation of a new mine
            if (game.createMine(pos)) {
              nb_mines.set(nb_mines.value + 1)
              total_cost_mines.set((total_cost_mines.value + game.mine_price))
              game.playerMoney.set(game.playerMoney.value - game.mine_price)
            }
          }
        }
        else if (buyingRailsMode) {
          if (game.rail_price <= game.playerMoney.value ){ //creation of a new mine
            if (game.createRail(pos)) {
              nb_rails.set(nb_rails.value + 1)
              total_cost_rails.set((total_cost_rails.value + game.rail_price))
              game.playerMoney.set(game.playerMoney.value - game.rail_price)
            }
          }
        }
        else {
          maybeClickRenderableAt(pos)
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
          text <== StringProperty("Time: ").concat(game.time.asString)
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

    onMouseClicked = (e: MouseEvent) => { requestFocus() }
  }
}
