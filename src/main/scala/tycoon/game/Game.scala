package tycoon.game

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.objects.vehicle._
import tycoon.objects.graph._
import tycoon.objects.carriage._
import tycoon.objects.landscape._
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

      //println("tycoon > game > Game.scala > GameLoop: 1.0 / elapsedTime + " FPS")

      update(elapsedTime)
    }
  }

  var map = new Map(map_width, map_height)
  var game_graph = new Graph
  val tilemap = new TileMap(map_width, map_height)
  tilemap.fillBackground(Tile.grassAndGround, map)

  /*
  def generateRandomMap() = {
    val r = new MapGenerator
    tilemap = r.generateRandomMap(this)
  }*/

  //Managers of game entities.
  var railManager = new RailManager(map, tilemap, game_graph)

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

  private val loop = new GameLoop()

  // INIT

  val tiledPane = new DraggableTiledPane(tilemap)
  tiledPane.moveToCenter()
  tiledPane.requestFocus()


/*
  entities.onChange((_, changes) => {
    for (change <- changes)
      change match {
        case Add(_, added) =>
          added.foreach(ent => tiledPane.addRenderable(ent))
        case Remove(_, removed) =>
          removed.foreach(ent => tiledPane.removeRenderable(ent))
        case Reorder(from, to, permutation) => ()
        case Update(pos, updated)           => ()
      }
  })*/

  private val player = new Player


  def start () : Unit = {
    player.money = 1000

    time set 0
    time_s = 0
    loop.start()
  }
  def pause () : Unit = {}
  def stop () : Unit = {}

  private def update(dt : Double) : Unit = {
    //update trains position here ?
    routes foreach { _.update(dt) }
    structures foreach { _.update(dt) }
    tiledPane.render()

    time_s += dt
    if (time_s > 1) {
      time_s -= 1
      time.set(time.value + 1)
    }
  }

  def playerName : StringProperty = player.name
  def playerName_= (new_name: String) = player.name = new_name

  def playerMoney : IntegerProperty = player.money
  def playerMoney_= (new_money: Int) = player.money = new_money

  def createStructure (kind: Int, pos: GridLocation) : Boolean = {
    var structure : Structure = new Town(pos, nb_structures, townManager)
    var additionalCondition = true
    kind match {
      case 0 => additionalCondition = tilemap.checkGrass(pos) && tilemap.checkGrass(new GridLocation(pos.row, pos.col+1))
      case _ => {
        structure = new Mine(pos, nb_structures)
        additionalCondition = tilemap.checkTile(pos, Tile.rock)
      }
    }

    if (tilemap.gridContains(structure.gridRect) && map.isUnused(structure.gridRect) && additionalCondition)
    {
      structure match {
        case t : Town => townManager.newTown(t)
        tilemap.addEntity(t, 0)
        case m : Mine => {
          mines += m
          townManager.newStructure(m)
          tilemap.addEntity(m, 0)
        }
        
        //tilemap.addTile(1, pos.col, pos.row, Tile.mine)
      }
      structures += structure
      //tiledPane.addRenderable(structure)
      map.add(structure.gridRect, structure)
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

  def removeAllTowns() : Unit = {
    towns.clear()
    // entities.clear()    TODO
    nb_structures = 0
    townManager.towns_list = new ListBuffer[Town]
    townManager.last_town = 0
  }

  def createMine (pos: GridLocation) : Boolean = {
    createStructure(1, pos)
  }
  def removeAllMines() : Unit = {
    mines.remove(mines.size-1)
   //  entities.remove(entities.size-1)    TODO
    nb_structures -= 1
  }

  // try to create rail at pos and return true in case of success
  def createRail(pos: GridLocation) : Boolean = {
    railManager.createRail(pos)
    // if returns true, remove money from player..
  }

  def removeLastRail() : Unit = { // TODO incomplet
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
  }

  def createTrain (town: Town) : Boolean = {
    // Carriage number set at 3 by default
    // + one good carriage now :)
    var train = new Train(town, 3)


    // check if there is an other train ??
    town.addTrain(train)
    trains += train
    //tiledPane.addRenderable(train)
    //tilemap.addTile(1, pos.col, pos.row, Tile.locomotive)
    tilemap.addEntity(train, 1)

    // paying
    playerMoney.set(playerMoney.value - train.cost)
    for (carriage <- train.carriages_list) {
      playerMoney.set(playerMoney.value - carriage.cost)
      tilemap.addEntity(carriage, 1)
      //tiledPane.addRenderable(carriage)
      //tilemap.addTile(1, pos.col, pos.row, Tile.passenger_wagon)
      carriages+=carriage
    }
    true
  }

  def createRoute (departure: Structure, arrival: Structure, train: Train) {
    val route = new Route(game_graph.shortestRoute(departure, arrival), train, this)
    routes += route
  }
}
