package tycoon.game

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.objects.vehicle.train._
import tycoon.objects.vehicle._
import tycoon.objects.graph._
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


import scala.xml.XML
import scala.io.Source
import java.io.{FileNotFoundException, IOException}
import scala.util.Try




class Game(val map_width : Int, val map_height : Int)
{
  def loadMap(filepath: String) : Boolean = {
    Try {
      val xml = XML.loadFile(filepath)

      println("All foods:")
      (xml \ "food" \ "name") foreach (i => println(i.text))
      println("\nSpecials:")
      for (food <- (xml \ "food"))
        if ((food \ "special").length > 0)
          println((food \ "special" \ "@id").text)
      println("\nSpecials again:")
      for (id <- (xml \ "food" \ "special"))
        println((id \ "@id").text)

    }.isSuccess
  }


  var infoTextTimer: Double = 0
  val informationText = StringProperty("")
  def clearInfoText(): Unit = setRandomInfoText()
  def setInfoText(s: String, duration: Double = 4): Unit = {
    informationText.set(s)
    infoTextTimer = duration
  }
  private def setRandomInfoText() = {
    val r = scala.util.Random
    val randomTexts = Seq(
      "random texts but i have no inspiration 1",
      "random texts but i have no inspiration 2",
      "random texts but i have no inspiration 3",
      "random texts but i have no inspiration 4",
      "random texts but i have no inspiration 5",
      "random texts but i have no inspiration 6",
      "random texts but i have no inspiration 7",
      "random texts but i have no inspiration 8",
      "random texts but i have no inspiration 9",
      "random texts but i have no inspiration 10"
    )
    setInfoText(randomTexts(r.nextInt(randomTexts.length)))
  }
  setRandomInfoText()


  // game map
  var game_graph = new Graph
  val map = new TileMap(map_width, map_height)
  map.fillBackground(Tile.grass)
  map.sprinkleTile(Tile.tree, 3)
  map.sprinkleTile(Tile.rock, 1)
  // map.generateLakes(5, 2000) SLOW

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
  var planes = new ListBuffer[Plane]()
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

    if (infoTextTimer > 0)
      infoTextTimer -= dt
    else clearInfoText()
  }








  // functions

  def playerName : StringProperty = _player.name
  def playerName_= (new_name: String) = _player.name = new_name

  def playerMoney : IntegerProperty = _player.money
  def playerMoney_= (new_money: Int) = _player.money = new_money




  /* KEEEEP BEGIN */

  def createStruct(struct: Structure, tilesAllowed: Array[Tile]): Boolean = {
    if (map.gridContains(struct.gridRect)
        && map.isUnused(struct.gridRect)
        && map.checkBgTiles(struct.gridRect, tilesAllowed)) {
      structures += struct
      map.addStructure(struct)
      nb_structures += 1
      game_graph.newStructure(struct)

      struct match {
        case town: Town => townManager.newTown(town)
        case mine: Mine => { mines += mine ; townManager.newStructure(mine) }
        case farm: Farm => ()
        case factory: Factory => ()
        case airport: Airport => ()
        case _ => ()
      }
      true
    }
    else false
  }

  def buyStruct(struct: BuyableStruct, pos: GridLocation, player: Player = _player): Boolean = {
    var bought: Boolean = false
    if (player.money.value >= struct.price) {
      struct.newInstance(pos, nb_structures, townManager) match {
        case town: Town => bought = createStruct(town, Tile.grass)
        case mine: Mine => bought = createStruct(mine, Array(Tile.rock))
        case farm: Farm => bought = createStruct(farm, Tile.grass)
        case factory: Factory => bought = createStruct(factory, Tile.grass)
        case airport: Airport => {
          //Airport is a Town facilitie, has to be contained in a town.
          val around = map.getSurroundingStructures(pos)
          for (neighbor <- around) {
            neighbor match {
              case town: Town => {
                if (!town.hasAirport && createStruct(airport, Tile.grass)) {
                  bought = true
                  town.hasAirport = true
                }
              }
              case _ => ()
            }
          }
        }
        case _ => ()
      }
    }
    if (bought) player.pay(struct.price)
    bought
  }

  def buyRail(rail: BuyableRail, pos: GridLocation, player: Player = _player): Boolean = {
    var bought: Boolean = false
    if (player.money.value >= rail.price) {
      rail.newInstance(pos) match {
        case rail: Rail => bought = railManager.createRail(rail)
        case _ => ()
      }
    }
    if (bought) player.pay(rail.price)
    bought
  }



  /* KEEEEP END */


  var nbTrains = IntegerProperty(0)


  def createTrain (town: Town) : Boolean = {
    var train = new Train(nbTrains.value, town, IntegerProperty(3), _player)

    town.addTrain(train)
    trains += train
    map.addEntity(train)

    nbTrains.set(nbTrains.value + 1)

    // paying
    playerMoney.set(playerMoney.value - train.cost)
    for (carriage <- train.carriageList) {
      playerMoney.set(playerMoney.value - carriage.cost)
      map.addEntity(carriage)
      carriages += carriage
    }
    true
  }

  def createPlane (airport: Airport) : Boolean = {
    var plane = new Plane(airport, _player)

    airport.addPlane(plane)
    planes += plane
    map.addEntity(plane)

    // paying
    playerMoney.set(playerMoney.value - plane.cost)
    true
  }

  def createRoute (departure: Structure, arrival: Structure, train: Train) {
    val route = new Route(game_graph.shortestRoute(departure, arrival), train)
    route.departure()
    routes += route
  }
  def createFly (departure: Structure, arrival: Structure, plane : Plane) {
    println ("tycoon > game > Game.scala > create Fly: creation of a fly betwenn to to airport with a plane ")
  }
}
