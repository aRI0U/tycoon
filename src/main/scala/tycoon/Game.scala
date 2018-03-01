

package tycoon

import tycoon.objects.structure._
import tycoon.objects.vehicle._
import tycoon.objects.Sprite
import tycoon.ui.{Tile, Renderable, DraggableTiledPane}
import tycoon.objects.game.Player

import javafx.animation.AnimationTimer
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer._
import scalafx.scene.image.Image
import scala.collection.mutable.{HashMap, ListBuffer}


import scalafx.Includes._
import scalafx.beans.property.{StringProperty, IntegerProperty}
import scalafx.beans.binding.Bindings


class Game(map_width : Int, map_height : Int)
{
  private class GameLoop extends AnimationTimer
  {
    var startNanoTime : Long = System.nanoTime()

    override def handle(currentNanoTime: Long) {
      var elapsedTime : Double = (currentNanoTime - startNanoTime) / 1000000000.0
      startNanoTime = currentNanoTime

      if (elapsedTime > 0.01)
        elapsedTime = 0.01

      //println(1000000000.0 / elapsedTime + " FPS")

      update(elapsedTime)
    }
  }

  var entities = new ObservableBuffer[Renderable]()

  var towns = new ListBuffer[Town]()
  var trains = new ListBuffer[Train]()

  private val loop = new GameLoop()

  // INIT
  val tilemap = new TileMap
  val padding = 4
  tilemap.setSize(map_width, map_height)
  tilemap.fill(Sprite.tiles_grass)
  tilemap.fillBorder(Sprite.tile_mine, 1) // TMP
  tilemap.fillBorder(Sprite.tile_rock, 2, 1)
  tilemap.fillBorder(Sprite.tiles_grass(1), 50, 3)
  val tiledPane = new DraggableTiledPane(tilemap, padding)

  private val player = new Player


  def start () : Unit = {
    tiledPane.moveToCenter()
    player.money = 1000
    loop.start()
  }
  def pause () : Unit = {}
  def stop () : Unit = {}

  private def update(dt : Double) : Unit = {

    // update game

  }

  def playerName : StringProperty = player.name
  def playerName_= (new_name: String) = player.name = new_name

  def playerMoney : IntegerProperty = player.money
  def playerMoney_= (new_money: Int) = player.money = new_money

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
