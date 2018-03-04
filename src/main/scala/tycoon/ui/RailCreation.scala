package tycoon.ui

import tycoon.{ Game, GridLocation }
import tycoon.objects.railway._

import scalafx.Includes._
import scalafx.scene.Scene

import scalafx.beans.property.{ StringProperty, IntegerProperty }
import scalafx.beans.binding.Bindings

import scalafx.scene.paint.Color._
import scalafx.scene.paint.{ Stops, LinearGradient }
import scalafx.scene.layout.{ BorderPane, VBox }
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, HPos, VPos, Insets, Rectangle2D, Orientation}
import scalafx.scene.control.{Button, Separator}
import scalafx.scene.image.{ Image, ImageView }
import scalafx.scene.input.{ KeyCode, KeyEvent, MouseEvent }
import scala.collection.mutable.ListBuffer

import scalafx.scene.input.MouseEvent

import scala.collection.mutable.HashMap
import scalafx.scene.control.{ TextField, Button }
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{ BorderPane, HBox, VBox}
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer._ // Add, Remove, Reorder, Update

class RailCreation(var game: Game) extends BorderPane {
  private var onFinished = new Runnable { def run() {} }

  def setOnFinished(r: Runnable) = {
    onFinished = r
  }

  // check whether click is simple click or drag
  private var _cost: IntegerProperty = IntegerProperty(0)
  def cost : IntegerProperty = _cost
  def cost_= (new_cost: Int) = _cost.set(new_cost)
  private var cost_str = new StringProperty
  cost_str <== cost.asString

//appenrantly gives 0.... now
  private var initial_money = game.playerMoney.get()

  // def playerMoney : IntegerProperty = player.money
  // def playerMoney_= (new_money: Int) = player.money = new_money

  private var mouse_anchor_x: Double = 0
  private var mouse_anchor_y: Double = 0

  def init(): Unit = {
    center = gamePane
    left = menuPane
    gamePane.center = game.tiledPane // update
  }

  private val gamePane = new BorderPane {

    style = "-fx-background-color: lightgreen"

    onMousePressed = (e: MouseEvent) => {
      requestFocus()

      mouse_anchor_x = e.x
      mouse_anchor_y = e.y
    }
    //var pos : GridLocation

    onMouseReleased = (e: MouseEvent) => {
      if (e.x == mouse_anchor_x && e.y == mouse_anchor_y) {
        val pos : GridLocation = game.tiledPane.screenPxToGridLoc(e.x, e.y)
        if (game.rail_price <= game.playerMoney.get() ){
          val pos : GridLocation = game.tiledPane.screenPxToGridLoc(e.x, e.y)
          if (game.createRail(pos)) {
            cost_= (_cost.get() + game.rail_price)
            game.playerMoney.set(game.playerMoney.get()-game.rail_price)
          }

        }
      }
    }
  }

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
        text <== StringProperty("Cost: $").concat(cost.asString)
        fill <== when ( _cost < initial_money) choose Green otherwise Red
        margin = Insets(10)
      },
      new Text {
        text = "explaining rules of  rail construction.."
        margin = Insets(10)
      },

      new Button {
        text = "Remove last rail"
        margin = Insets(10)
        onMouseClicked = (e: MouseEvent) => {
          game.removeAllRails()
          game.playerMoney.set(game.playerMoney.get() + game.rail_price)
          cost_= (_cost.get() - game.rail_price)
        }
      },
      new Button {
        text = "Exit construction"
        margin = Insets(10)
        onMouseClicked = (e: MouseEvent) => {
          onFinished.run()
        }
      }
    )
    onMouseClicked = (e: MouseEvent) => { requestFocus() }
  }

}
