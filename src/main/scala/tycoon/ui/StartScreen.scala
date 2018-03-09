package tycoon.ui

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.geometry.{Pos, Insets}
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.scene.text.Text
import scalafx.scene.media.AudioClip

// MUSICS: http://freemusicarchive.org/genre/Chiptune/?sort=track_interest

class StartScreen extends BorderPane
{
  private var onStart = new Runnable { def run() {} }
  private var onOpenCredits = new Runnable { def run() {} }

  def setOnStart(r : Runnable) = {
    onStart = r
  }
  def setOnOpenCredits(r : Runnable) = {
    onOpenCredits = r
  }

  stylesheets += "style/startscreen.css"
  id = "body"


  //private val theme = new AudioClip("file:src/main/resources/startscreen_music.mp3")
  //theme.cycleCount = 1


  def init() = {
    //theme.play()
  }

  center = new VBox {
    alignment = Pos.Center
    children = Seq(
      new Text {
        alignment = Pos.Center
        text = "Tycoon Game"
        margin = Insets(50)
        id = "game_title"
      },
      new HBox {
        alignment = Pos.Center
        children = Seq(
          new Button {
            text = "New Game"
            onMouseClicked = _ => onStart.run()
            margin = Insets(20)
            styleClass += "bevel-grey"
          },
          new Button {
            text = "Exit"
            onMouseClicked = _ => Platform.exit()
            margin = Insets(20)
            styleClass += "bevel-grey"
          },
          new Button {
            text = "Credits"
            onMouseClicked = _ => { //theme.stop() ;
              onOpenCredits.run() }
            margin = Insets(20)
            styleClass += "bevel-grey"
          }
        )
      }
    )
  }
}
