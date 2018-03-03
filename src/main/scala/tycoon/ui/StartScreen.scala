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


  private val theme = new AudioClip("file:src/main/resources/startscreen_music.mp3")
  theme.cycleCount = 10000


  def init() = {
    theme.play()
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
            onMouseClicked = _ => { theme.stop() ; onOpenCredits.run() }
            margin = Insets(20)
            styleClass += "bevel-grey"
          }
        )
      }
    )
  }
}


/*

class StartScreen extends BorderPane
{
  private var onStart = new Runnable { def run() {} }

  private var iliketrains = new AudioClip("file:src/main/resources/trains.mp3")

  def setOnStart(r : Runnable) = {
    onStart = r
  }
  var bg = new Image("file:src/main/resources/Train_screen1.jpeg")
  val bg_trains = new Image("file:src/main/resources/trains.jpg")


  // style = "-fx-background-image"
    //"-fx-background-color: linear-gradient(blue, pink, blue)"
  background = new Background(Array(new BackgroundImage(bg, BackgroundRepeat.NoRepeat, BackgroundRepeat.NoRepeat,
    BackgroundPosition.Center, BackgroundSize.Default)))
  center = new HBox {
    def changebg() = { super.background = new Background(Array(new BackgroundImage(bg_trains, BackgroundRepeat.NoRepeat, BackgroundRepeat.NoRepeat,
      BackgroundPosition.Center, BackgroundSize.Default))) }

    alignment = Pos.Center
    children = Seq(
      new Button {
        text = "New Game"
        margin = Insets(50)
        minWidth = 200
        style = """-fx-background-color: #000000,
                      linear-gradient(#7ebcea, #2f4b8f),
                      linear-gradient(#426ab7, #263e75),
                      linear-gradient(#395cab, #223768);
                  -fx-background-insets: 0,1,2,3;
                  -fx-background-radius: 3,2,2,2;
                  -fx-padding: 12 30 12 30;
                  -fx-text-fill: white;
                  -fx-font-size: 18px;
                  -fx-effect: dropshadow( one-pass-box , rgba(0,0,0,0.8) , 0, 0.0 , 0 , 1);"""
        onMouseClicked = (e: MouseEvent) => onStart.run()
      },
      new Button {
        text = "Exit"
        margin = Insets(50)
        minWidth = 200
        style = """-fx-background-color: #000000,
                      linear-gradient(#7ebcea, #2f4b8f),
                      linear-gradient(#426ab7, #263e75),
                      linear-gradient(#395cab, #223768);
                  -fx-background-insets: 0,1,2,3;
                  -fx-background-radius: 3,2,2,2;
                  -fx-padding: 12 30 12 30;
                  -fx-width: 300;
                  -fx-text-fill: white;
                  -fx-font-size: 18px;
                  -fx-effect: dropshadow( one-pass-box , rgba(0,0,0,0.8) , 0, 0.0 , 0 , 1);"""
        onMouseClicked = (e: MouseEvent) => Platform.exit()
      },
      new Button {
        text = "i like trains"
        margin = Insets(50)
        minWidth = 200
        style = """-fx-background-color: pink;
                  -fx-background-insets: 0,1,2,3;
                  -fx-background-radius: 3,2,2,2;
                  -fx-padding: 12 30 12 30;
                  -fx-width: 300;
                  -fx-text-fill: purple;
                  -fx-font-size: 18px;
                  -fx-effect: dropshadow( one-pass-box , rgba(0,0,0,0.8) , 0, 0.0 , 0 , 1);"""
        onMouseClicked = (e: MouseEvent) => { changebg() ; iliketrains.play()
         }
      }
    )
  }

}
*/
