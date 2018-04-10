package tycoon.ui // #

import scala.collection.mutable.ListBuffer

import tycoon.game._
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle.Vehicle
import tycoon.objects.vehicle.train._

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.beans.property.{StringProperty, IntegerProperty, BooleanProperty}
import scalafx.beans.binding.Bindings
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer._
import scalafx.geometry._
import scalafx.geometry.Orientation._
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.input.{MouseEvent, KeyEvent}
import scalafx.scene.layout._
import scalafx.scene.paint.Color._
import scalafx.scene.text.Text


class GameScreen(val game: Game) extends BorderPane
{
  stylesheets += "style/gamescreen.css"
  id = "body"


  /** Interaction Menu (Bottom) */

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
  interactionsMenu.addBuyableRoad(BuyableRoad.Water)

  interactionsMenu.addBuyableVehicle(BuyableVehicle.Train)
  interactionsMenu.addBuyableVehicle(BuyableVehicle.Plane)
  interactionsMenu.addBuyableVehicle(BuyableVehicle.Boat)
  interactionsMenu.addBuyableVehicle(BuyableVehicle.Truck)


  /** Information Pane (Top) */

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


  /** Game Pane (Center) */

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


  /** Menu Pane (Left) */

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

  // init map
  def init(): Unit = gamePane.center = game.tiledPane


  /** Print data in menuPane using bindPrintData & Communicate with interactions menu */

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
          case _ => ()
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
      datas foreach { content =>
        val txt = new Text {
          text <== StringProperty(content.label)
          margin = Insets(5)
        }
        children.add(txt)
        content.data foreach { elt =>
          val item = new Text {
            elt match {
              case rankedElt: PrintableRankedElement => {
                text <== when (rankedElt.visible) choose (StringProperty(elt.name + ": ").concat(elt.valueStr)) otherwise (StringProperty(""))
                rankedElt match {
                  case p: PrintableTownProduct => fill <== when (p.valueInt > 0) choose Green otherwise Red
                  case _ => ()
                }
              }
              case _ => text <== StringProperty(elt.name + ": ").concat(elt.valueStr)
            }
            margin = Insets(8)
          }
          children.add(item)
        }
      }
    }
    menuPane.center = new ScrollPane { content = printData }
  }
}
