package tycoon.game

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.objects.vehicle._
import tycoon.objects.graph._
import tycoon.objects.carriage._
import tycoon.game._
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

  //Managers of game entities.
  var railManager = new RailCreationManager()

/* new game loop pattern:
  double t = 0.0;
  double dt = 1 / 60.0;

  double currentTime = hires_time_in_seconds();

  while ( !quit )
  {
      double newTime = hires_time_in_seconds();
      double frameTime = newTime - currentTime;
      currentTime = newTime;

      while ( frameTime > 0.0 )
      {
          float deltaTime = min( frameTime, dt );
          integrate( state, t, deltaTime );
          frameTime -= deltaTime;
          t += deltaTime;
      }

      render( state );
  }
*/


  var entities = new ObservableBuffer[Entity]()

  var game_map = new Map(map_width, map_height)
  var game_graph = new Graph

  var nb_structures = 0

  val mine_price = 200
  val rail_price = 10

  var rails = new ListBuffer[Rail]()
  var structures = new ListBuffer[Structure]()
  var mines = new ListBuffer[Mine]()
  var towns = new ListBuffer[Town]()
  var trains = new ListBuffer[BasicTrain]()
  var routes = new ListBuffer[Route]()
  var carriages = new ListBuffer[Carriage]()

  private val loop = new GameLoop()

  // INIT
  val tilemap = new TileMap(map_width, map_height)
  tilemap.fill(Tile.grass)
  tilemap.tilesWidth = Tile.square_width
  tilemap.tilesHeight = Tile.square_height
  val tiledPane = new DraggableTiledPane(tilemap)
  tiledPane.requestFocus()



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
    for (structure <- structures) // towns.foreach(update...)
    {
      structure.update(dt)
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
        structures += structure
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

// Ability to set a rail if next to a stucture or  a road_head
  def createRail (pos: GridLocation) : Boolean = {

    new GridRectangle(new GridLocation(0, 0), 1, 1)
    // check whether rail is within the map boundaries
    if (tilemap.gridContains(new GridRectangle(pos, 1, 1)))
    {
      //Rail initialised with a staight sprite.
      val rail = new BasicRail(pos, 0)
      var valid = true
      //Watch for location conflict.
      for (other <- entities) {
        if (other.gridIntersects(rail))
          valid = false
      }
      // Call of IsSetable
      valid = valid & railManager.IsSetable(rail,entities,rails,game_graph)
      // In case of no problem:
      if (valid) {
        rails += rail
        entities += rail
        game_map.addToMap(pos, true)
      }
      valid
    }
    else false
  }

  def removeLastRails() : Unit = {
    //add some temporary list if deletion has to be made
    val removed_rail = rails(rails.size-1)
    if (removed_rail.road.finished == true ) {
      removed_rail.road.finished = false
      routes.remove(routes.size -1)
    }
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
    for (carriage <- train.carriages_list) {
      carriage match {
        case p:PassengerCarriage =>
         playerMoney.set(playerMoney.get() + p.passengers * p.ticket_price)
        case _ => ()
      }
    }
    val route = new Route(game_graph.shortestRoute(departure, arrival), train, this)
    routes += route
  }
}
