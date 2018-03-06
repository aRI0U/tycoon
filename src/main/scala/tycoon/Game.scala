package tycoon

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.objects.vehicle._
import tycoon.objects.graph._
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

      //if (elapsedTime > 0.01)
      //    elapsedTime = 0.01

      //println(1.0 / elapsedTime + " FPS")

      update(elapsedTime)
    }
  }

  var entities = new ObservableBuffer[Renderable]()

  var game_map = new Map(map_width, map_height)
  var game_graph = new Graph

  var nb_structures = 0

  val mine_price = 200
  val rail_price = 10

  var rails = new ListBuffer[Rail]()
  var mines = new ListBuffer[Mine]()
  var towns = new ListBuffer[Town]()
  var trains = new ListBuffer[BasicTrain]()
  var routes = new ListBuffer[Route]()

  private val loop = new GameLoop()

  // INIT
  val tilemap = new TileMap
  val padding = 4
  tilemap.setSize(map_width, map_height)
  tilemap.fill(Sprite.tiles_grass)
  tilemap.fillBorder(Sprite.tile_tree, 1) // TMP
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
    //update trains position here ?
    for (route <- routes)
    {
      route.update(dt)
    }
    for (town <- towns)
    {
      town.update(dt)
    }
    tiledPane.layoutEntities

  }

  def playerName : StringProperty = player.name
  def playerName_= (new_name: String) = player.name = new_name

  def playerMoney : IntegerProperty = player.money
  def playerMoney_= (new_money: Int) = player.money = new_money

  def createStructure (kind: Int, pos: GridLocation) : Boolean = {
    var structure : Structure = new BasicTown(pos, nb_structures)
      kind match {
      case 0 => ;
      case _ => structure = new Mine(pos, nb_structures)
    }
    // check whether town is within the map boundaries
    if (tilemap.gridRect.contains(structure.gridRect))
    {
      // if so, check whether it intersects with an other town
      var valid = true
      for (other <- entities) {
        if (other.gridIntersects(structure))
          valid = false
      }
      if (valid) {
        structure match {
          case t : Town => towns += t
          case m : Mine => mines += m
        }
        entities += structure
        nb_structures += 1
        game_graph.newStructure(structure)
      }
      valid
    }
    else false
  }


  def createTown (pos: GridLocation) : Boolean = {
    createStructure(0, pos)
  }

  def removeAllTowns() : Unit = {
    towns.clear()
    entities.clear()
    nb_structures = 0
  }

  def createMine (pos: GridLocation) : Boolean = {
    createStructure(1, pos)
  }
  def removeAllMines() : Unit = {
    mines.remove(mines.size-1)
    entities.remove(entities.size-1)
    nb_structures -= 1
  }


  ///TODO/////def createTrail (pos: GridLocation) : Boolean = {


//Rail become a trail_head if it is next to a town (see later for train station), or if it is conected to a tail_head
  def createRail (pos: GridLocation) : Boolean = {
    //depending of the situation should choose here between straight and turning rail
    val rail = new BasicRail(pos, 0)

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
      //left is probably actualy below ect...
      var boxleft = new GridLocation(pos.column, pos.row + 1)
      var boxright = new GridLocation(pos.column, pos.row-1)
      var boxup  = new GridLocation(pos.column+1, pos.row)
      var boxbelow  = new GridLocation(pos.column-1, pos.row)
      val boxes = Array(boxleft,boxup,boxright,boxbelow)
      for (other <- entities) {
        for (i : Int <- 0 to 3) {
          if (other.gridContains(boxes(i)) ) {
            val pair = (other,i)
            env += pair
          }
        }
      }
      def turning(o : Int, d : Int,previous_rail_update : BasicRail) : Unit = {
        if ((o == 3 && d == 0) ||(d == 1 && o == 2) )
          previous_rail_update.tile.getView.rotate = 180
        if ((o == 0 && d == 1) ||(d == 2 && o == 3) )
          previous_rail_update.tile.getView.rotate = 90
        if ((o == 1 && d == 0) ||(d == 3 && o == 2) )
          previous_rail_update.tile.getView.rotate = 270
      }
      def checkType(pair : Any) = pair match {
        case (t: Town,i : Int) => {
          if (rail.road.start_town == None) {
            rail.road.start_town = Some(t)
          }
          rail.road.end_town = Some(t)
          if (!(rail.road.end_town == rail.road.start_town)) {
            rail.road.end_town = Some(t)
            rail.road.finished = true
            for (rail_member <- rail.road.rails) {
              rail_member.road = rail.road
            }
          }
          if (rail.road.finished) {
            false
          }
          else true
        }
        //transmission of road properties from the prévious rail to the next one
        case (previous_rail: BasicRail,i : Int)=> {
          if ((previous_rail.road_head == true) && (previous_rail.road.finished == false)) {
              rail.road.rails ++= previous_rail.road.rails
              rail.road.length += previous_rail.road.length
              println (rail.road.rails)

              rail.previous = previous_rail
              previous_rail.next = rail

              if (rail.road.start_town == None) {
                rail.road.start_town = previous_rail.road.start_town
              }
              else {
                if (!(rail.road.start_town == previous_rail.road.start_town)) {
                  rail.road.end_town = rail.road.start_town
                  rail.road.start_town = previous_rail.road.start_town
                  rail.road.finished = true
                  for (rail_member <- rail.road.rails) {
                    rail_member.road = rail.road
                  }
                }
              }
              previous_rail.road_head = false

              rail.origin = i
              previous_rail.orientation = i
              if ((previous_rail.origin +  previous_rail.orientation) % 2 ==1) {
                entities-= previous_rail
                rails-=previous_rail
                val previous_rail_update = new BasicRail(previous_rail.pos, 1)
                entities+= previous_rail_update
                rails+= previous_rail_update
                turning(previous_rail.origin,previous_rail.orientation,previous_rail_update)
                previous_rail_update.road_head = false
              }
              if (rail.origin == 1 || rail.origin == 3) {
                rail.tile.getView.rotate = 90
              }
              true
          }
          else false
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
        game_map.addToMap(pos, true)
      }
      valid & valid_bis
    }
    else false
  }

  def removeAllRails() : Unit = {
    //add some temporary list if deletion has to be made
    rails.remove(rails.size-1)
    entities.remove(entities.size-1)
    // TODO: actualize data in the graph
  }
  def createTrain (town: Town) : Boolean = {
    var train = new BasicTrain(town)
    // check if there is an other train ??
    var valid = true
    /*if (tilemap.gridRect.contains(mine.gridRect))
    {
      // if so, check whether it intersects with an other entity
      var valid = true
      for (other <- entities) {
        if (other.gridIntersects(mine))
          valid = false
      }
      */
      if (valid) {
        trains += train
        entities += train
      }
      valid
  }

  def createRoute (departure: Structure, arrival: Structure, train: Train) {
    val route = new Route(game_graph.shortestRoute(departure, arrival), train)
    routes += route
  }
}
