package tycoon

import tycoon.ui._

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.{Scene, PerspectiveCamera, Camera}
import scalafx.scene.layout.StackPane


object Main extends JFXApp {
  val game = new Game(100, 100)

  val startScreen = new StartScreen()

  val content = new StackPane()
  content.getChildren().add(startScreen)
  startScreen.init()

  val appScene = new Scene(new StackPane(content))

  val appStage = new PrimaryStage {
    title = "Tycoon Game"
    resizable = true
    //maximized = true
    minWidth = 700
    minHeight = 500
    scene = appScene
  }

  stage = appStage

  startScreen.setOnStart(new Runnable {
    def run() {
      val gameCreationScreen = new GameCreationScreen(game)

      gameCreationScreen.setOnValidate(new Runnable {
        def run() {

          val gameScreen = new GameScreen(game)

          content.getChildren().clear()
          content.getChildren().add(gameScreen)
          gameScreen.init()
          game.start()
        }
      })

      content.getChildren().clear()
      content.getChildren().add(gameCreationScreen)
      gameCreationScreen.init()
    }
  })

  startScreen.setOnOpenCredits(new Runnable {
    def run() {
      val creditsScreen = new CreditsScreen()

      creditsScreen.setOnExit(new Runnable {
        def run() {
          content.getChildren().clear()
          content.getChildren().add(startScreen)
          startScreen.init()
          //appScene.camera = new Camera
          appStage.resizable = true
        }
      })

      content.getChildren().clear()
      content.getChildren().add(creditsScreen)
      appScene.camera = new PerspectiveCamera
      //creditsScreen.init()
      appStage.resizable = false
    }
  })



}
