package tycoon.ui


import scalafx.Includes._
import scalafx.scene.Scene

import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Stops, LinearGradient}
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, Insets}
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent

class StartScreen extends Scene
{
  private var onStart = new Runnable { def run() {} }

  def setOnStart(r : Runnable) = {
    onStart = r
  }



  root = new BorderPane {
    style = "-fx-background-color: linear-gradient(blue, pink, blue)"
    center = new HBox {
      alignment = Pos.Center
      children = Seq(
        new Button {
          text = "New Game"
          margin = Insets(50)
          onMouseClicked = (e: MouseEvent) => onStart.run()
        },
        new Button {
          text = "Exit"
          margin = Insets(50)
        }
      )
    }
  }
}
