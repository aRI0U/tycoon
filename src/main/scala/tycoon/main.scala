
/* TODO

rename Renderable into Entity and merge it with Printable

Printable -> print data of every entity clicked on even if several are overlaping

abstract class EntityManager
--tableau d'entités, possibilité de supprimer le dernier elt ajouté ou tous...
--function pour le linké au tableau observable entities pr que les 2 restent pareils si on supprime ds 1 ca supp ds l'autre

et en faire hériter RailManager qui va gérer toutes les rotations des rails etc

TownManager qui comprend la fonction de création de town etc

*/

// c'est le AudioClip qui consomme tout le CPU





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
    def run() = {
      val gameCreationScreen = new GameCreationScreen(game)

      gameCreationScreen.setOnValidate(new Runnable {
        def run() = {
          val gameScreen = new GameScreen(game)

          content.getChildren().clear()
          content.getChildren().add(gameScreen)
          gameScreen.init()
          game.start()
          println("hi")
        }
      })

      content.getChildren().clear()
      content.getChildren().add(gameCreationScreen)
      gameCreationScreen.init()
    }
  })

  startScreen.setOnOpenCredits(new Runnable {
    def run() = {
      val creditsScreen = new CreditsScreen()

      creditsScreen.setOnExit(new Runnable {
        def run() = {
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
