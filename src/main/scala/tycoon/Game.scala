

package tycoon

import tycoon.objects.structure._
import tycoon.objects.vehicle._
import tycoon.objects.Sprite
import tycoon.ui.{Tile, Renderable}

import javafx.animation.AnimationTimer
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer._
import scalafx.scene.image.Image
import scala.collection.mutable.{HashMap, ListBuffer}


class Game
{
  private class GameLoop extends AnimationTimer
  {
    var startNanoTime : Long = System.nanoTime()

    override def handle(currentNanoTime: Long) {
      var elapsedTime : Double = (currentNanoTime - startNanoTime) / 1000000000.0
      startNanoTime = currentNanoTime

      if (elapsedTime > 0.01)
        elapsedTime = 0.01

      println(1000000000.0 / elapsedTime + " FPS")

      update(elapsedTime)
    }
  }

  var entities = new ObservableBuffer[Renderable]()

  var towns = new ListBuffer[Town]()
  var trains = new ListBuffer[Train]()

  private val loop = new GameLoop()

  val tilemap = new TileMap
  val padding = 4
  def init (map_width : Int, map_height : Int) : Unit = {
    tilemap.setSize(map_width, map_height)
    tilemap.fill(Sprite.tile_grass)
    tilemap.fillBorder(Sprite.tile_tree, 50)
  }
  def start () : Unit = {
    loop.start()
  }
  def pause () : Unit = {}
  def stop () : Unit = {}

  private def update(dt : Double) : Unit = {

    // update game

  }

  def create_town (pos: GridLocation) : Unit = {
    /*val town = new BasicTown(case_x, case_y)
    towns += town
    entities += town*/
  }




}
