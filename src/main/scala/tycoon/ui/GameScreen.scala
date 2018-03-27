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
import scalafx.scene.control.{Button, Separator, ButtonType, Alert, TextArea, Label, Slider, Tab, TabPane}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{MouseEvent, KeyEvent}

import scala.collection.mutable.{HashMap, HashSet}
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer._ // Add, Remove, Reorder, Update

import scalafx.scene.control.{TextField, Button, ScrollPane}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, HBox, VBox, GridPane, Priority}

import scalafx.beans.property.{StringProperty, IntegerProperty, BooleanProperty}
import scalafx.beans.binding.Bindings
import scala.collection.mutable.ListBuffer


class GameScreen(val game: Game) extends BorderPane
{
  stylesheets += "style/gamescreen.css"
  id = "body"

  private val interactionsMenu = new InteractionsMenu(game)

// removes: TODO
  interactionsMenu.addBuyableStruct(BuyableStruct.SmallTown)
  interactionsMenu.addBuyableStruct(BuyableStruct.MediumTown)
  interactionsMenu.addBuyableStruct(BuyableStruct.LargeTown)
  interactionsMenu.addBuyableStruct(BuyableStruct.Mine)
  interactionsMenu.addBuyableStruct(BuyableStruct.Farm)
  interactionsMenu.addBuyableStruct(BuyableStruct.Factory)

  def init () : Unit = {
    center = gamePane //
    gamePane.center = game.tiledPane
    left = menuPane // can be moved out
    bottom = interactionsMenu // can be moved out
  }


  private val gamePane = new BorderPane {
    private var mouseAnchorX: Double = 0
    private var mouseAnchorY: Double = 0
    private var specialDragging: Boolean = false

    onMousePressed = (e: MouseEvent) => {
      requestFocus()
      mouseAnchorX = e.x
      mouseAnchorY = e.y

      if (e.clickCount == 2) {
        game.tiledPane.isDraggable = false
        specialDragging = true
      }
    }

    onMouseDragged = (e: MouseEvent) => {
      if (specialDragging) {
        val pos: GridLocation = game.tiledPane.screenPxToGridLoc(e.x, e.y)
        interactionsMenu.mousePressed(pos, dragging = true)
      }
    }

    onMouseReleased = (e: MouseEvent) => {
      game.tiledPane.isDraggable = true
      specialDragging = false

      if (Math.abs(e.x - mouseAnchorX) <= 5 && Math.abs(e.y - mouseAnchorY) <= 5) {

        val pos = game.tiledPane.screenPxToGridLoc(e.x, e.y)
        interactionsMenu.mousePressed(pos)

        maybeClickRenderableAt(pos)
      }
    }
  }





  /* to be beautified */

  private var tripCreationMode : Boolean = false
  private var firstStructureSelected = BooleanProperty(false)
  private var firstStructure : Structure = _

  private var buyingTrainMode : Boolean = false

  private def init_buyingTrains () {
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





  def maybeClickRenderableAt(pos: GridLocation): Unit = {
    game.map.getContentAt(pos) match {
      case Some(entity) => {
        entity match {
          case train : Train => {
            println("tycoon > ui > GameScreen.scala > maybeClickRenderableAt: " + train.carriageList)
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
                  firstStructure.getTrain match {
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
    val printData = new VBox {
      for (elt <- data) {
        val item = new Text {
          text <== StringProperty(elt._1 + ": ").concat(elt._2)
          margin = Insets(5)
        }
        children.add(item)
      }
    }
    menuPane.center = new ScrollPane {
      content = printData
    }
  }

  private val actionsPane = new VBox {
    children = Seq(
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


  private val menuPane = new BorderPane {
    id = "menu"

    bottom = new VBox {
      children = Seq(
        new Separator { orientation = Orientation.Horizontal ; styleClass += "sep" },
        new Text {
          text <== StringProperty("Time: undefined")
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
        new QuitButtons
      )
    }

    top = actionsPane

    onMouseClicked = (e: MouseEvent) => { requestFocus() }
  }


}
