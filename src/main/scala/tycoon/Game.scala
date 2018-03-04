

package tycoon

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.objects.vehicle._
import tycoon.ui.Sprite
import tycoon.ui.{Tile, Renderable, DraggableTiledPane}

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

  val mine_price = 200
  val rail_price = 10

  var rails = new ListBuffer[Rail]()
  var mines = new ListBuffer[Mine]()
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

    for (town <- towns)
    {
      town.update(dt)
    }

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
  def createMine (pos: GridLocation) : Boolean = {
    val mine = new Mine(pos)
    // check whether mine is within the map boundaries
    if (tilemap.gridRect.contains(mine.gridRect))
    {
      // if so, check whether it intersects with an other entity
      var valid = true
      for (other <- entities) {
        if (other.gridIntersects(mine))
          valid = false
      }
      if (valid) {
        mines += mine
        entities += mine
      }
      valid
    }
    else false
  }
  def removeAllMines() : Unit = {
    mines.clear()
    entities.clear()
  }


  ///TODO/////def createTrail (pos: GridLocation) : Boolean = {


//Rail become a trail_head if it is next to a town (see later for train station), or if it is conected to a tail_head
  def createRail (pos: GridLocation) : Boolean = {
    //depending of the situation should choos here between straight and turning rail
    val rail = new BasicRail(pos)

    // check whether rail is within the map boundaries
    if (tilemap.gridRect.contains(rail.gridRect))
    {
      // if so, check whether it intersects with an other entity
      var valid = true
      for (other <- entities) {
        if (other.gridIntersects(rail))
          valid = false
      }
      //looking for a trail_head around there
      //list of surounding entities (Renderable)
      var env = new ListBuffer[Any]
      var boxleft = new GridLocation(pos.column, pos.row + 1)
      var boxright = new GridLocation(pos.column, pos.row-1)
      var boxup  = new GridLocation(pos.column+1, pos.row)
      var boxbelow  = new GridLocation(pos.column-1, pos.row)
      val boxes = Array(boxleft,boxup,boxright,boxbelow)
      // boxleft.row = 1 + boxleft.row
      // boxright.row-=1
      // boxup.collumn+=1
      // boxbelow.collumn-=1
      for (other <- entities) {
        for (i : Int <- 0 to 3) {
          if (other.gridContains(boxes(i)) ) {
            val pair = (other,i)
            env += pair
          }
        }
      }
      def orientation(o : Int, d : Int) : Int = {
        //gives an integer between 0 and 5 acording to the coresponding orientation of the rail
        if ((o == 0 && d == 3) ||(d == 0 && o == 3) )
          return 1
        if ((o == 1 && d == 4) ||(d == 1 && o == 4) )
          return 0
        else return 2
      }
      def checkType(pair : Any) = pair match {
        case (t: Town,i : Int) => true
        case (r: BasicRail,i : Int)=> {
          rail.origin = i
          r.orientation = orientation(r.origin, i)
          if (r.orientation ==1) {
            r.tile_update = new Tile(Sprite.tile_straight_rail2)
          }
          true
            //We determin the orientation of the previous rail by combinating rail.origin and here i the direction of the folowing.
            //So we conclude about rail.orientation
          // i match {
            // case 0 => r
            // case 1 =>
            // case 2 =>
            // case 3 =>
          //}
        }
        case _ => false
      }
      var valid_bis = false
      for (pair <- env) {
        if (checkType(pair))
          valid_bis = true
      }
      if (valid &&  valid_bis) {
        rails += rail
        entities += rail
      }
      valid & valid_bis
    }
    else false
  }

  def removeAllRails() : Unit = {
    //add some temporary list if deletion has to be made
    rails.clear()
    entities.clear()
  }

}
