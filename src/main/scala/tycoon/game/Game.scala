package tycoon.game

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.objects.vehicle._
import tycoon.objects.graph._
import tycoon.objects.carriage._
import tycoon.game._
import tycoon.ui.Tile
import tycoon.ui.{Tile, Renderable, DraggableTiledPane}

import javafx.animation.AnimationTimer
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer._
import scalafx.scene.image.Image
import scala.collection.mutable.{HashMap, ListBuffer}


import scalafx.Includes._
import scalafx.beans.property.{StringProperty, IntegerProperty}
import scalafx.beans.binding.Bindings


class Game(val map_width : Int, val map_height : Int)
{


  // game map
  var game_graph = new Graph
  val map = new TileMap(map_width, map_height)
  map.fillBackground(Tile.grass)
  map.sprinkleTile(Tile.tree, 3)
  map.sprinkleTile(Tile.rock, 1)
  map.generateLakes(10, 5000)

  val tiledPane = new DraggableTiledPane(map)
  tiledPane.moveToCenter()
  tiledPane.requestFocus()


  // game objects
  var railManager = new RailManager(map, game_graph)
  var townManager = new TownManager()

  var nb_structures = 0
  val mine_price = 200
  val rail_price = 10

  var rails = new ListBuffer[Rail]()
  var structures = new ListBuffer[Structure]()
  var mines = new ListBuffer[Mine]()
  var towns = new ListBuffer[Town]()
  var trains = new ListBuffer[Train]()
  var routes = new ListBuffer[Route]()
  var carriages = new ListBuffer[Carriage]()

  private val _player = new Player





  // game loop

  private class GameLoop extends AnimationTimer
  {
    var startNanoTime : Long = System.nanoTime()

    override def handle(currentNanoTime: Long) {
      var elapsedTime : Double = (currentNanoTime - startNanoTime) / 1000000000.0
      startNanoTime = currentNanoTime

      //if (elapsedTime > 0.01)
      //    elapsedTime = 0.01
      //println("tycoon > game > Game.scala > GameLoop: 1.0 / elapsedTime + " FPS")

      update(elapsedTime)
    }
  }

  private val loop = new GameLoop()

  def start () : Unit = {
    playerMoney = 1000000 // move into player.init()
    loop.start()
  }
  def pause () : Unit = {}
  def stop () : Unit = {}

  private def update(dt : Double) : Unit = {
    //update trains position here ?
    routes foreach { _.update(dt) }
    structures foreach { _.update(dt) }
    tiledPane.render()
  }








  // functions

  def playerName : StringProperty = _player.name
  def playerName_= (new_name: String) = _player.name = new_name

  def playerMoney : IntegerProperty = _player.money
  def playerMoney_= (new_money: Int) = _player.money = new_money



  // try to create rail at pos and return true in case of success
  def createRail(pos: GridLocation) : Boolean = {
     railManager.createRail(pos)
    // if returns true, remove money from player..
  }


  /* KEEEEP BEGIN */

  def createStruct(struct: Structure, tilesAllowed: Array[Tile]): Boolean = {
    if (map.gridContains(struct.gridRect)
        && map.isUnused(struct.gridRect)
        && map.checkBgTiles(struct.gridRect, tilesAllowed)) {
      structures += struct
      map.add(struct, 0)
      nb_structures += 1
      game_graph.newStructure(struct)

      struct match {
        case town: Town => townManager.newTown(town)
        case mine: Mine => { mines += mine ; townManager.newStructure(mine) }
        case farm: Farm => ()
        case factory: Factory => ()
        case _ => ()
      }
      true
    }
    else false
  }

  def buyStruct(struct: BuyableStruct, pos: GridLocation, player: Player = _player): Boolean = {
    var bought: Boolean = false
    if (player.money.value >= struct.price) {
      struct.newInstance(pos, nb_structures) match {
        case town: Town => bought = createStruct(town, Tile.grass)
        case mine: Mine => bought = createStruct(mine, Array(Tile.rock))
        case farm: Farm => bought = createStruct(farm, Tile.grass)
        case factory: Factory => bought = createStruct(factory, Tile.grass)
        case _ => ()
      }
    }
    if (bought) player.pay(struct.price)
    bought
  }

  /* KEEEEP END */


  def createTrain (town: Town) : Boolean = {
    var train = new Train(town, 3)

    town.addTrain(train)
    trains += train
    map.add(train, 1)

    // paying
    playerMoney.set(playerMoney.value - train.cost)
    for (carriage <- train.carriageList) {
      playerMoney.set(playerMoney.value - carriage.cost)
      map.add(carriage, 1)
      carriages+=carriage
    }
    true
  }

  def createRoute (departure: Structure, arrival: Structure, train: Train) {
    val route = new Route(game_graph.shortestRoute(departure, arrival), train, this)
    routes += route
  }
}
