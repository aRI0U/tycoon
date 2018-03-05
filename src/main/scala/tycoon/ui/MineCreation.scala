package tycoon.ui

import tycoon.{ Game, GridLocation }
import tycoon.objects.structure.Mine

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

class MineCreation(var game: Game) extends BorderPane {
  private var onFinished = new Runnable { def run() {} }

  def setOnFinished(r: Runnable) = {
    onFinished = r
  }
  //private val tiledPane = new DraggableTiledPane(game.tilemap, game.padding)

  // game.entities.onChange((_, changes) => {
  //   for (change <- changes)
  //     change match {
  //       case Add(_, added) =>
  //         added.foreach(town => game.tiledPane.addEntity(town))
  //       case Remove(_, removed) =>
  //         removed.foreach(town => game.tiledPane.removeEntity(town))
  //       case Reorder(from, to, permutation) => ()
  //       case Update(pos, updated) => ()
  //     }
  // })


  // private val min_towns: Int = 2
  // private val max_towns: Int = 5
  //
  private var nb_mines = IntegerProperty(0)
  private var nb_mines_str = new StringProperty
  nb_mines_str <== nb_mines.asString

  // check whether click is simple click or drag
  private var _cost: IntegerProperty = IntegerProperty(0)
  def cost : IntegerProperty = _cost
  def cost_= (new_cost: Int) = _cost.set(new_cost)
  private var cost_str = new StringProperty
  cost_str <== cost.asString

  private var initial_money = game.playerMoney.get()

  // def playerMoney : IntegerProperty = player.money
  // def playerMoney_= (new_money: Int) = player.money = new_money

  private var mouse_anchor_x: Double = 0
  private var mouse_anchor_y: Double = 0

  def init(): Unit = {
    center = gamePane
    left = menuPane
    gamePane.center = game.tiledPane // update
    _cost.set( 0)
    nb_mines.set(0)
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
        //if (game.mine_price < game.playerMoney) {
        val pos : GridLocation = game.tiledPane.screenPxToGridLoc(e.x, e.y)
        if (game.mine_price <= game.playerMoney.get() ){
          val pos : GridLocation = game.tiledPane.screenPxToGridLoc(e.x, e.y)
          if (game.createMine(pos)) {
            nb_mines.set(nb_mines.get() + 1)
            cost_= (_cost.get() + game.mine_price)
            game.playerMoney.set(game.playerMoney.get()-game.mine_price)
          }
        //creation of a new mine
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
        fill <== when (_cost < initial_money) choose Green otherwise Red
        margin = Insets(10)
      },

      new Text {
        text = "explaining rules of  mine construction.."
        margin = Insets(10)
      },

      new Button {
        text = "Remove"
        margin = Insets(10)
        onMouseClicked = (e: MouseEvent) => {
          if (nb_mines.get()>0) {
            nb_mines.set(nb_mines.get()-1)
            game.removeAllMines()
            game.playerMoney.set(game.playerMoney.get()+game.mine_price)
            cost_= (_cost.get() - game.mine_price)
          }
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
