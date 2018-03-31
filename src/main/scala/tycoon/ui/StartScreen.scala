package tycoon.ui

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.scene.text.Text


class StartScreen extends BorderPane
{
  private var onStartGameCreation = new Runnable { def run() {} }
  private var onLoadGame = new Runnable { def run() {} }
  private var onOpenCredits = new Runnable { def run() {} }

  def setOnStartGameCreation(r: Runnable) = onStartGameCreation = r
  def setOnLoadGame(r: Runnable) = onLoadGame = r
  def setOnOpenCredits(r: Runnable) = onOpenCredits = r

  stylesheets += "style/startscreen.css"
  id = "startScreenBody"

  private val gameNameText = new Text {
    id = "gameName"
    text = "Tycoon Game"
    margin = Insets(50)
  }

  private val startGameButton = new Button {
    text = "New Game"
    onMouseClicked = _ => onStartGameCreation.run()
    margin = Insets(20)
  }
  private val loadGameButton = new Button {
    text = "Load Game"
    onMouseClicked = _ => onLoadGame.run()
    margin = Insets(20)
  }
  private val exitButton = new Button {
    text = "Exit"
    onMouseClicked = _ => Platform.exit()
    margin = Insets(20)
  }
  private val openCreditsButton = new Button {
    text = "Credits"
    onMouseClicked = _ => onOpenCredits.run()
    margin = Insets(20)
  }

  center = new VBox {
    alignment = Pos.Center
    children = Seq(
      gameNameText,
      new HBox {
        alignment = Pos.Center
        children = Seq(
          startGameButton,
          loadGameButton,
          exitButton,
          openCreditsButton
        )
      }
    )
  }
}
