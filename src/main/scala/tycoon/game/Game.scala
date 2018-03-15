package tycoon.game

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.objects.vehicle._
import tycoon.objects.graph._
import tycoon.objects.carriage._
import tycoon.ui.Tile
import tycoon.ui.{Tile, Entity, DraggableTiledPane}

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
  var time = IntegerProperty(0)
  var time_s : Double = 0

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
      tiledPane.layoutEntities
    }
  }


  var entities = new ObservableBuffer[Entity]()

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
  var carriages = new ListBuffer[Carriage]()

  private val loop = new GameLoop()

  // INIT
  val tilemap = new TileMap(map_width, map_height)
  tilemap.fill(Tile.grass)
  tilemap.tiles_width = Tile.square_width
  tilemap.tiles_height = Tile.square_height
  val tiledPane = new DraggableTiledPane(tilemap)
  tiledPane.requestFocus



  entities.onChange((_, changes) => {
    for (change <- changes)
      change match {
        case Add(_, added) =>
          added.foreach(ent => tiledPane.addEntity(ent))
        case Remove(_, removed) =>
          removed.foreach(ent => tiledPane.removeEntity(ent))
        case Reorder(from, to, permutation) => ()
        case Update(pos, updated)           => ()
      }
  })

  private val player = new Player


  def start () : Unit = {
    tiledPane.moveToCenter()
    player.money = 1000

    time.set(0)
    time_s = 0
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
    //tiledPane.layoutEntities

    time_s += dt
    if (time_s > 1) {
      time_s -= 1
      time.set(time.get() + 1)
    }
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
    if (tilemap.gridContains(structure.gridRect))
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

//Rail become a trail_head if it is next to a town (see later for train station), or if it is conected to a tail_head
  def createRail (pos: GridLocation) : Boolean = {
    //depending of the situation should choose here between straight and turning rail
    val rail = new BasicRail(pos, 0)

    // check whether rail is within the map boundaries
    if (tilemap.gridContains(rail.gridRect))
    {
      // if so, check whether it intersects with an other entity
      var valid = true
      for (other <- entities) {
        if (other.gridIntersects(rail))
          valid = false
      }
      var rail_met : Int = 0
      //looking for a trail_head around there
      //list of surounding entities (Renderable)
      var env = new ListBuffer[Any]
      //left is probably actualy below ect...
      var boxleft = new GridLocation(pos.col, pos.row + 1)
      var boxright = new GridLocation(pos.col, pos.row-1)
      var boxup  = new GridLocation(pos.col+1, pos.row)
      var boxbelow  = new GridLocation(pos.col-1, pos.row)
      val boxes = Array(boxleft,boxup,boxright,boxbelow)
      for (other <- entities) {
        for (i : Int <- 0 to 3) {
          if (other.gridContains(boxes(i)) ) {
            val pair = (other,i)
            env += pair
          }
        }
      }
      def turning(o : Int, d : Int, rail_to_update : BasicRail) : Unit = {
        if ((o == 3 && d == 0) ||(d == 1 && o == 2) ) {
          rail_to_update.tile.getView.rotate = 180
          rail_to_update.nb_rotation = 0
        }
        if ((o == 0 && d == 1) ||(d == 2 && o == 3) ) {
          rail_to_update.tile.getView.rotate = 90
          rail_to_update.nb_rotation = 3
        }
        if ((o == 1 && d == 0) ||(d == 3 && o == 2) ){
          rail_to_update.tile.getView.rotate = 270
          rail_to_update.nb_rotation = 0
        }
      }
      def track_mergence (track : ListBuffer[Rail]) : Unit = {
        for (r <- track) {
          var temp =  r.next
          r.next = r.previous
          r.previous = temp
        }
      }
      def checkType(pair : Any) = pair match {
        case (t: Town,i : Int) => {
          rail.orientation = i
          if (rail.road.start_town == None) {
            rail.road.start_town = Some(t)
            if (rail_met == 0) {
              rail.origin = i
            }
          }
          rail.road.end_town = Some(t)
          if (!(rail.road.end_town == rail.road.start_town)) {
            rail.road.end_town = Some(t)
            rail.road.finished = true
            rail.orientation = i

            for (rail_member <- rail.road.rails) {
              rail_member.road = rail.road

              rail_member.printData += Tuple2("Between", StringProperty(rail.road.start_town.get.name))
              rail_member.printData += Tuple2("and", StringProperty(rail.road.end_town.get.name))}
          }
          if (rail.road.finished) {
            false
          }
          else true
        }
        //transmission of road properties from the previous rail to the next one
        case (previous_rail: BasicRail,i : Int)=> {
          if ((previous_rail.road_head == true) && (previous_rail.road.finished == false)) {
            if (rail_met < 3) {
              rail_met+=1
            }
              rail.road.rails ++= previous_rail.road.rails
              rail.road.length += previous_rail.road.length
              println (rail.road.rails)

              if (rail_met == 1) {
                rail.previous = previous_rail
                previous_rail.next = rail
              }
              else {
                // In case of rails track mergence, it's get unified (rail_met > 1)
                track_mergence(previous_rail.road.rails)
                rail.next = previous_rail
                previous_rail.previous = rail
                track_mergence(rail.road.rails)
              }
              if (rail.road.start_town == None) {
                rail.road.start_town = previous_rail.road.start_town
              }
              else {
                // In this case, the road is finished
                if (!(rail.road.start_town == previous_rail.road.start_town)) {
                  rail.road.end_town = rail.road.start_town
                  rail.road.start_town = previous_rail.road.start_town
                  rail.road.finished = true

                  game_graph.newRoad(rail.road)
                  for (rail_member <- rail.road.rails) {
                    rail_member.road = rail.road

                    rail_member.printData += Tuple2("Between", StringProperty(rail.road.start_town.get.name))
                    rail_member.printData += Tuple2("and", StringProperty(rail.road.end_town.get.name))
                  }
                }
              }
              previous_rail.road_head = false

              rail.origin = i
              previous_rail.orientation = i
              // Choos a new tile for previous rail if turning
              //actualy the update rail is not the  one used most part of the time... the one below stays..
              if ((previous_rail.origin +  previous_rail.orientation) % 2 ==1) {
                entities-= previous_rail
                rails-=previous_rail
                val previous_rail_update = previous_rail.copy(tile_type = 1)
                // previous_rail.next.previous = previous_rail_update
                // previous_rail.previous.next = previous_rail_update
                // val previous_rail_update = previous_rail.copy(previous_rail.pos, 1)
                previous_rail_update.previous = previous_rail.previous
                previous_rail_update.next = previous_rail.next
                previous_rail_update.road = previous_rail.road
                entities+= previous_rail_update
                rails+= previous_rail_update
                turning(previous_rail.origin,previous_rail.orientation,previous_rail_update)
                previous_rail_update.road_head = false
              }
              // If needed, turn the straight rile tile
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
      if ((rail.origin == 1 || rail.origin == 3) && rail.road.length == 1 ) {
        rail.tile.getView.rotate = 90
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
    rails(rails.size-1).road_head = true
  }

  def createTrain (town: Town) : Boolean = {
    // Carriage number set at 3 by default
    // + one good carriage now :)
    var train = new BasicTrain(town, 3)


    // check if there is an other train ??
    town.addTrain(train)
    trains += train
    entities += train

    // paying
    playerMoney.set(playerMoney.get() - train.cost)
    for (carriage <- train.carriages_list) {
      playerMoney.set(playerMoney.get() - carriage.cost)
      entities+=carriage
      carriages+=carriage
    }
    true
  }

  def createRoute (departure: Structure, arrival: Structure, train: Train) {
   train.boarding()
    for (carriage <- train.carriages_list) {
      playerMoney.set(playerMoney.get() + carriage.passengers * carriage.ticket_price)
      println(carriage.passengers)
    }
    val route = new Route(game_graph.shortestRoute(departure, arrival), train, this)
    println (game_graph.shortestRoute(departure, arrival))
    routes += route
    println (route.current_road)
  }
}
