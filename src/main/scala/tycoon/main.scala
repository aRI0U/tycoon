package tycoon

import tycoon.ui.{StartScreen, GameCreationScreen, GameScreen}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.StackPane


object Main extends JFXApp {
  val game = new Game()
  game.init(50, 10)

  val startScreen = new StartScreen()
  val gameCreationScreen = new GameCreationScreen(game)
  val gameScreen = new GameScreen(game)

  val content = new StackPane()
  content.getChildren().add(startScreen)

  stage = new PrimaryStage {
    title = "Tycoon Game"
    resizable = true
    //maximized = true
    minWidth = 600
    minHeight = 400
    scene = new Scene(new StackPane(content))
  }

  startScreen.setOnStart(new Runnable {
    def run() {
      content.getChildren().clear()
      content.getChildren().add(gameCreationScreen)
    }
  })
}
