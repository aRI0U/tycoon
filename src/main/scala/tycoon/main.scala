// getClass.getResource("/testData.txt").getPath
// BETTER (2.12.x): import scala.io.Source
// val readmeText : Iterator[String] = Source.fromResource("readme.txt").getLines

// http://www.scalafx.org/docs/properties/
// https://github.com/scalafx/ProScalaFX/blob/master/src/proscalafx/ch04/reversi/ui/Reversi.scala
package tycoon

import tycoon.ui.{StartScreen, GameScreen}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage

import scalafx.scene.Scene
import scalafx.scene.layout.StackPane

//


import javafx.scene.input.MouseEvent
import javafx.event.EventHandler
import javafx.animation.AnimationTimer

import scalafx.event.{EventHandler,ActionEvent}
import scalafx.geometry.Insets
import scalafx.collections.ObservableBuffer
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Stops, LinearGradient}
import scalafx.scene.text.Text
import scalafx.scene.image.{Image,ImageView}
import scalafx.scene.control._


object Main extends JFXApp {


  val game = new Game()

  val content = new StackPane()

  val startScreen = new StartScreen()
  val gameScreen = new GameScreen(game)

  content.getChildren().add(startScreen)

  stage = new PrimaryStage {
    title = "Tycoon"
    resizable = true
    maximized = true
    minWidth = 600
    minHeight = 400
    scene = new Scene(new StackPane(content))
  }

  startScreen.setOnStart(new Runnable {
    def run() {
      content.getChildren().clear()
      content.getChildren().add(gameScreen)
      // stage.maximized = true
      // game.start()
    }
  })

  // https://gist.github.com/james-d/a249470377fb3c58784a9349a22641c4

  // var/val : public (visible from outside) ; nothing : private

}









/*
  val img = new ImageView {
    image = new Image("http://www.mariouniverse.com/images/sprites/nes/smb2/birdo.png")
    //scaleX <== when (hover) choose 1 otherwise 0.5
    //scaleY <== when (pressed) choose 1 otherwise 0.5
  }

  val timeline = new Timeline {
    cycleCount = Timeline.Indefinite
    autoReverse = true
    keyFrames = Seq(
      at(0 s) {img.scaleX -> 0.5},
      at(0 s) {img.scaleY -> 0.5},
      at(2 s) {img.scaleX -> 1},
      at(1 s) {img.scaleY -> 1}
    )
  }

  //timeline.play()
  img.onMouseEntered = new EventHandler[MouseEvent] {
    override def handle(e: MouseEvent) = timeline.play()
  }
  img.onMouseClicked = new EventHandler[MouseEvent] {
    override def handle(e: MouseEvent) = timeline.stop()
  }*/


  /* pane.handleEvent(MouseEvent.Any) {
    e: MouseEvent => {
      e.eventType match {
        case MouseEvent.MouseEntered => { timeline.play() }
        case MouseEvent.MouseClicked => { timeline.stop() }
        case _                       => {}
      }
    } */

    /*


  stage = new PrimaryStage {
    title = "Hello World"
    scene = new Scene(600, 400) {



      fill = Black
      content = new HBox {
        padding = Insets(20)
        children = Seq(
          new Text {
            text = "Hello "
            style = "-fx-font-size: 48pt"
            fill = new LinearGradient(
              endX = 0,
              stops = Stops(PaleGreen, SeaGreen))
          },
          img,
          new Text {
            text = "World!!!"
            style = "-fx-font-size: 48pt"
            fill = new LinearGradient(
              endX = 0,
              stops = Stops(Cyan, DodgerBlue)
            )
            effect = new DropShadow {
              color = DodgerBlue
              radius = 25
              spread = 0.25
            }
          }
        )
      }
    }
  }

} */
