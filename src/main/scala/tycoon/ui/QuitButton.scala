package tycoon.ui // #

import tycoon.game.{Game, GridLocation, GridRectangle}
import tycoon.objects.structure._

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Alert, Button, ButtonType}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.Text
import scala.xml._
import scala.xml.transform.RuleTransformer
import scala.xml.transform.RewriteRule
import scalafx.stage.FileChooser
import scalafx.application.JFXApp.PrimaryStage
import scalafx.stage.{FileChooser, Stage}
import javafx.scene.control.TextInputDialog
import java.io.File

import java.io.PrintWriter


class QuitButton(game : Game) extends VBox
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
      },
      new Button {
        text = "Save game"
        margin = Insets(5)
        onMouseClicked = (e: MouseEvent) => {
          val dialog = new TextInputDialog()
          dialog.setTitle("Save Game")
          dialog.setHeaderText("Name of your game :")
          val result = dialog.showAndWait()
          val file = new File(result.get + ".xml")
          game.saver.createSaveFile(file,result.get())
        }
      }
    )
  }
}
