package tycoon

import tycoon.ui.{StartScreen, GameCreationScreen, GameScreen, RailCreation, MineCreation}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.StackPane


object Main extends JFXApp {
  val game = new Game(30, 10)

  val startScreen = new StartScreen()
  val gameCreationScreen = new GameCreationScreen(game)
  val gameScreen = new GameScreen(game)
  val railCreation = new RailCreation(game)
  val mineCreation = new MineCreation(game)

  val content = new StackPane()
  content.getChildren().add(startScreen)

  stage = new PrimaryStage {
    title = "Tycoon Game"
    resizable = true
    //maximized = true
    minWidth = 700
    minHeight = 500
    scene = new Scene(new StackPane(content))
  }

  startScreen.setOnStart(new Runnable {
    def run() {
      content.getChildren().clear()
      content.getChildren().add(gameCreationScreen)
      gameCreationScreen.init()
    }
  })

  gameCreationScreen.setOnValidate(new Runnable {
    def run() {
      content.getChildren().clear()
      content.getChildren().add(gameScreen)
      gameScreen.init()
      game.start()
    }
  })

  gameScreen.setOnRailClick(new Runnable {
    def run() {
      content.getChildren().clear()
      content.getChildren().add(railCreation)
      railCreation.init()
      //railCreation.init()
    }
  })

  gameScreen.setOnMineClick(new Runnable {
    def run() {
      content.getChildren().clear()
      content.getChildren().add(mineCreation)
      mineCreation.init()
      //railCreation.init()
    }
  })

  mineCreation.setOnFinished(new Runnable {
    def run() {
      content.getChildren().clear()
      content.getChildren().add(gameScreen)
      gameScreen.init()
    }
  })

  railCreation.setOnFinished(new Runnable {
    def run() {
      content.getChildren().clear()
      content.getChildren().add(gameScreen)
      gameScreen.init()
    }
  })
}
