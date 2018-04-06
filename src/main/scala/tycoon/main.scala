package tycoon

import tycoon.game.Game
import tycoon.ui.{CreditsScreen, GameScreen, StartScreen}

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.{PerspectiveCamera, Scene}
import scalafx.scene.layout.{Pane, StackPane}
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

object Settings { // to be moved
  val gameWidth = 500
  val gameHeight = 500
}



object Main extends JFXApp {
  val game = new Game(Settings.gameWidth, Settings.gameHeight)

  val startScreen = new StartScreen()
  val gameScreen = new GameScreen(game)
  val creditsScreen = new CreditsScreen()

  val content = new StackPane()
  val appScene = new Scene(new StackPane(content))

  val appStage = new PrimaryStage {
    title = "Tycoon Game"
    resizable = true
    // maximized = true
    minWidth = 800
    minHeight = 600
    scene = appScene
  }
  stage = appStage

  // initial screen
  content.getChildren().add(startScreen)

  def switchScreen(newScreen: Pane) = {
    content.getChildren.clear()
    content.getChildren.add(newScreen)
  }

  startScreen.setOnStartGameCreation(new Runnable {
    def run() = {
      switchScreen(gameScreen)
      game.fillNewGame()
      gameScreen.init()
      game.start()
    }
  })

  startScreen.setOnLoadGame(new Runnable {
    def run() = {
      val fileChooser = new FileChooser {
       title = "Open Game Save"
       extensionFilters.setAll(new ExtensionFilter("Tycoon Save Files (*.xml)", "*.xml"))
      }
      var selectedFile = fileChooser.showOpenDialog(stage)
      do {
        try {
          if (game.loadMap(selectedFile.toString())) {
            switchScreen(gameScreen)
            gameScreen.init()
            game.start()
            selectedFile = null
          }
          else {
            println("Game backup couldn't be opened or read. ")
            selectedFile = fileChooser.showOpenDialog(stage)
          }
        } catch {
          case e: NullPointerException => ()
        }
      }
      while (selectedFile != null)
    }
  })

  startScreen.setOnOpenCredits(new Runnable {
    def run() = {
      switchScreen(creditsScreen)
      appScene.camera = new PerspectiveCamera
      appStage.resizable = false
    }
  })

  creditsScreen.setOnExit(new Runnable {
    def run() = {
      switchScreen(startScreen)
      appStage.resizable = true
    }
  })
}
