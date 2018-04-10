package tycoon.ui // #

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Alert, Button, ButtonType}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.Text


class QuitButton extends VBox
{
  alignment = Pos.BottomCenter

  children = new HBox {
    children = Seq(
      new Button {
        text = "Quit Game"
        margin = Insets(5)

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
