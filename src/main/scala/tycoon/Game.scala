

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
    tilemap.fillBorder(Sprite.tile_tree, 1)
    tilemap.fillBorder(Sprite.tile_rock, 2, 1)
    tilemap.fillBorder(Sprite.tile_grass, 50, 3)
  }
  def start () : Unit = {
    loop.start()
  }
  def pause () : Unit = {}
  def stop () : Unit = {}

  private def update(dt : Double) : Unit = {

    // update game

  }

  def setPlayerName (name: String) : Unit = {
    // TODO class Player
  }

  def createTown (pos: GridLocation) : Boolean = {
    val town = new BasicTown(pos)
    // check whether town is within the map boundaries
    if (tilemap.gridRect.contains(town.gridRect))
    {
      // if so, check whether it intersects with an other town
      var valid = true
      for (other <- towns) {
        if (other.gridIntersects(town))
          valid = false
      }
      if (valid) {
        towns += town
        entities += town
      }
      valid
    }
    else false
  }

  def removeAllTowns() : Unit = {
    towns.clear()
    entities.clear()
  }




}
