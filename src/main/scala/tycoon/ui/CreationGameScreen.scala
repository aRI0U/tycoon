package tycoon.ui

import tycoon.Game
import tycoon.DraggableTiledPane


import scalafx.Includes._
import scalafx.scene.Scene

import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Stops, LinearGradient}
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, Insets, Rectangle2D}
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scala.collection.mutable.ListBuffer

import scalafx.scene.input.MouseEvent

import scala.collection.mutable.HashMap


//val tileset = new Image("file:src/main/resources/Town1.")

class CreationGameScreen(var game : Game) extends BorderPane
{
  game.entities.onChange((source, changes) => {
    println("entity added")






  })


   
  center = new BorderPane {
    final val tileset = new Image("file:src/main/resources/tileset.png")
    var tiledPane = new DraggableTiledPane(game.tilemap, game.map_min_col, game.map_max_col, game.map_min_row, game.map_max_row)

    style = "-fx-background-color: lightgreen"
    center = tiledPane

    //Creation of a City 
    onMouseClicked = (e: MouseEvent) => {
      val (x,y) = tiledPane.pixelToCase(e.getSceneX(),e.getSceneY())
      /*
      def f( t :Town) :Boolean =  {if ((t.pos_X,t.pos_Y) == (x,y)) {return true} else return false}
      if ((x,y) == game.townsList.find(f_)) {*/
      game.create_town (x,y)

    }

    onKeyPressed = (k: KeyEvent) => k.code match {
      case KeyCode.E =>
        System.out.println("e key was pressed");
      case _ =>
    }
  }
}
