package tycoon.ui

import tycoon.game._
import tycoon.objects.vehicle.train._
import tycoon.objects.vehicle.Vehicle
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

  interactionsMenu.addBuyableStruct(BuyableStruct.SmallTown)
  interactionsMenu.addBuyableStruct(BuyableStruct.MediumTown)
  interactionsMenu.addBuyableStruct(BuyableStruct.LargeTown)
  interactionsMenu.addBuyableStruct(BuyableStruct.Mine)
  interactionsMenu.addBuyableStruct(BuyableStruct.Farm)
  interactionsMenu.addBuyableStruct(BuyableStruct.Factory)
  interactionsMenu.addBuyableStruct(BuyableStruct.PackingPlant)
  interactionsMenu.addBuyableStruct(BuyableStruct.Airport)
  interactionsMenu.addBuyableStruct(BuyableStruct.Field)
  interactionsMenu.addBuyableStruct(BuyableStruct.Dock)

  interactionsMenu.addBuyableRoad(BuyableRoad.Rail)
  interactionsMenu.addBuyableRoad(BuyableRoad.Asphalt)
  interactionsMenu.addBuyableRoad(BuyableRoad.Grass)

  interactionsMenu.addBuyableVehicle(BuyableVehicle.Train)
  interactionsMenu.addBuyableVehicle(BuyableVehicle.Plane)
  interactionsMenu.addBuyableVehicle(BuyableVehicle.Boat)
  interactionsMenu.addBuyableVehicle(BuyableVehicle.Truck)

  private val informationPane = new BorderPane {
    id = "informationPane"
    center = new Text {
      margin = Insets(5)
      text <== game.informationText
    }
    left = new QuitButton
    right = new HBox {
      alignment = Pos.Center
      children = Seq(
        new Text {
          text <== game.fps.asString
          wrappingWidth = 20
        },
        new Text(" FPS"),
        new Button {
          text = "<<"
          margin = Insets(5, 5, 5, 10)
          onMouseClicked = { _ => game.decreaseSpeed() }
        },
        new Text {
          text <== game.speedMultiplier.asString.concat("x")
          margin = Insets(5)
        },
        new Button {
          text = ">>"
          margin = Insets(5)
          onMouseClicked = { _ => game.increaseSpeed() }
        }
      )
    }
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
        mousePressed(pos)
      }
    }
  }

  def mousePressed(pos: GridLocation): Unit = {
    var debugStr = "tycoon > ui > GameScreen.scala > mousePressed: "
    debugStr += "pos(" + pos.col.toString + ", " + pos.row.toString + ")"
    debugStr += ", background(" + game.map.getBackgroundTile(pos).name + ")"
    debugStr += ", structure("

    game.map.maybeGetStructureAt(pos) match {
      case None => ()
      case Some(e) => {
        debugStr += e.tile.name
        bindPrintData(e.printData)
        e match {
          case struct: Structure => interactionsMenu.structureClicked(struct)
          case _ => () // rail
        }
      }
    }

    debugStr += "), entities("

    game.map.getEntitiesAt(pos) foreach { e => debugStr += e.tile.name + ", " }
    if (debugStr.takeRight(1) != "(") debugStr = debugStr.dropRight(2)

    debugStr += ")"
    println(debugStr)
  }

  def bindPrintData(datas: ListBuffer[PrintableData]) {
    val printData = new VBox {
      for (content <- datas) {
        val section = new Text {
          text <== StringProperty(content.label)
          margin = Insets(5)
        }
        children.add(section)
        for (elt <- content.data) {
          val item = new Text {
            text <== when (elt._3) choose (StringProperty(elt._1 + ": ").concat(elt._2)) otherwise (StringProperty(""))
            margin = Insets(8)
          }
          children.add(item)
        }
      }
    }
    menuPane.center = new ScrollPane {
      content = printData
    }
  }



  // def bindPrintData(data: ListBuffer[(String, StringProperty)]) {
  //   val printData = new VBox {
  //     for (elt <- data) {
  //       val item = new Text {
  //         text <== StringProperty(elt._1 + ": ").concat(elt._2)
  //         margin = Insets(5)
  //       }
  //       children.add(item)
  //     }
  //   }
  //   menuPane.center = new ScrollPane {
  //     content = printData
  //   }
  // }

  private val menuPane = new BorderPane {
    id = "menu"

    minWidth = 175
    maxWidth = 175

    bottom = new VBox {
      children = Seq(
        new Separator { orientation = Orientation.Horizontal ; styleClass += "sep" },
        new Text {
          text <== game.elapsedTimeStr
          margin = Insets(5)
        },
        new Text {
          text <== StringProperty("Player: ").concat(game.playerName)
          margin = Insets(5)
        },
        new Text {
          text <== StringProperty("Balance: $").concat(game.playerFormattedMoney)
          fill <== when (game.playerMoney > 0) choose Green otherwise Red
          margin = Insets(5)
        }

      )
    }

    onMouseClicked = (e: MouseEvent) => { requestFocus() }
  }

  left = menuPane
  bottom = interactionsMenu
  top = informationPane
  center = gamePane

  def init(): Unit = gamePane.center = game.tiledPane
}
