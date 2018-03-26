package tycoon.ui



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

import scalafx.beans.property.{StringProperty, IntegerProperty}
import scalafx.beans.binding.Bindings
import scala.collection.mutable.ListBuffer


class QuitButtons extends VBox
{
  alignment = Pos.BottomCenter

  children = new HBox {
    children = Seq(
     
      new Button {
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
      }
    )
  }
}
