package tycoon.ui

import tycoon.Game


import scalafx.Includes._
import scalafx.scene.Scene

import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Stops, LinearGradient}
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, Insets, Rectangle2D}
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}

import scalafx.scene.input.MouseEvent

import scala.collection.mutable.HashMap


//val tileset = new Image("file:src/main/resources/Town1.")

class CreationGameScreen(var game : Game) extends BorderPane
{
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
  center = new BorderPane {
    onMouseClicked = (e: MouseEvent) => {
      val (x,y) = mouse_to_box(e)
      game.create_town (x,y)
    }
  }
}
