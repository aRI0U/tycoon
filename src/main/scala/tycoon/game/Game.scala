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

  private val player = new Player





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
    player.money = 1000 // move into player.init()
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

  def playerName : StringProperty = player.name
  def playerName_= (new_name: String) = player.name = new_name

  def playerMoney : IntegerProperty = player.money
  def playerMoney_= (new_money: Int) = player.money = new_money

  def createStructure (kind: Int, pos: GridLocation) : Boolean = {
    var structure : Structure = new Town(pos, nb_structures, townManager)
    var additionalCondition = true
    kind match {
      case 0 => additionalCondition = map.checkBgTile(pos, Tile.grass) && map.checkBgTile(pos.right, Tile.grass)
      case _ => {
        structure = new Mine(pos, nb_structures)
        additionalCondition = map.checkBgTile(pos, Tile.rock)
      }
    }

    if (map.gridContains(structure.gridRect) && map.isUnused(structure.gridRect) && additionalCondition)
    {
      structure match {
        case t : Town => townManager.newTown(t)
        case m : Mine => {
          mines += m
          townManager.newStructure(m)
        }

        //map.addTile(1, pos.col, pos.row, Tile.mine)
      }
      structures += structure
      //tiledPane.addRenderable(structure)
      map.add(structure, 0)
      nb_structures += 1
      game_graph.newStructure(structure)
      true
    }
    else
      false
  }


  def createTown (pos: GridLocation) : Boolean = {
    createStructure(0, pos)
  }

  def removeAllTowns() : Boolean = {
    towns.clear()
    // entities.clear()    TODO
    nb_structures = 0
    townManager.towns_list = new ListBuffer[Town]
    townManager.last_town = 0
    true
  }

  def createMine (pos: GridLocation) : Boolean = {
    createStructure(1, pos)
  }
  def removeAllMines() : Boolean = {
    mines.remove(mines.size-1)
   //  entities.remove(entities.size-1)    TODO
    nb_structures -= 1
    true
  }

  // try to create rail at pos and return true in case of success
  def createRail(pos: GridLocation) : Boolean = {
    railManager.createRail(pos)
    // if returns true, remove money from player..
  }

  def removeLastRail() : Boolean = { // TODO incomplet
    //add some temporary list if deletion has to be made
    val removedRail = rails(rails.size - 1)
    if (removedRail.road.finished == true ) {
      removedRail.road.finished = false
      routes.remove(routes.size - 1)
    }
    if (removedRail.previous != removedRail)
      removedRail.previous.next = removedRail.previous
    rails.remove(rails.size - 1)
    // entities.remove(entities.size-1)   // TODO
    true
  }

  def createTrain (town: Town) : Boolean = {
    // Carriage number set at 3 by default
    // + one good carriage now :)
    var train = new Train(town, 3)


    // check if there is an other train ??
    town.addTrain(train)
    trains += train
    //tiledPane.addRenderable(train)
    //map.addTile(1, pos.col, pos.row, Tile.locomotive)
    map.add(train, 1)

    // paying
    playerMoney.set(playerMoney.value - train.cost)
    for (carriage <- train.carriageList) {
      playerMoney.set(playerMoney.value - carriage.cost)
      map.add(carriage, 1)
      //tiledPane.addRenderable(carriage)
      //map.addTile(1, pos.col, pos.row, Tile.passenger_wagon)
      carriages+=carriage
    }
    true
  }

  def createRoute (departure: Structure, arrival: Structure, train: Train) {
    val route = new Route(game_graph.shortestRoute(departure, arrival), train, this)
    routes += route
  }
}
