package tycoon.ui

import tycoon.Game

import scalafx.scene.Scene

import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Stops, LinearGradient}
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, Insets}
import scalafx.scene.control.Button


class GameScreen(var game : Game) extends Scene
{


  root = new BorderPane {
    style = "-fx-background-color: lightgreen"
    center = new VBox {
      // alignment = Pos.CENTER
      children = Seq(
        new Text {
          text = "Buy train"
        }
      )
    }
  }
  fill = Black
}
