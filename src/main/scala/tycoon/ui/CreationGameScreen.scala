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
  /*
  style = "-fx-background-color: lightblue;"

  //Valeur relative au déplacement, à modifier. En pixels.
  val shift_x : Int = 0
  val shift_y : Int = 0

  //Taille d'une case en pixels.
  val size : Int = 32

  def mouse_to_box(e : MouseEvent) : (Int,Int) = {
    val x  = Math.round( e.getSceneX()).toInt/32
    val y  = Math.round( e.getSceneX()).toInt/32
    return (x,y)
    
  }
  */
   
  center = new BorderPane {
    final val tileset = new Image("file:src/main/resources/tileset.png")
    var tiledPane = new DraggableTiledPane(32, 32, tileset)

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
