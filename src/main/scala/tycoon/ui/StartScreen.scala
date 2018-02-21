package tycoon.ui


import scalafx.Includes._
import scalafx.scene.{Scene, Node}

import scalafx.application.Platform

import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Stops, LinearGradient}
import scalafx.scene.layout.{BorderPane, HBox, StackPane, BackgroundImage, BackgroundRepeat, BackgroundPosition, BackgroundSize, Background}
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, Insets}
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafx.scene.media._
import scalafx.scene.image.{Image, ImageView}

import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.PieChart

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
      },
      new PieChart {
        title = "I like trains"
        data = ObservableBuffer(
          PieChart.Data("TRAINS", 20),
          PieChart.Data("TRAINS", 30),
          PieChart.Data("TRAINS", 10),
          PieChart.Data("TRAINS", 40)
        )
      }
    )
  }

}
