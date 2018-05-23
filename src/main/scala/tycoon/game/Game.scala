package tycoon.game

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.objects.vehicle.train._
import tycoon.objects.vehicle._
import tycoon.objects.graph._
import tycoon.game.ai._
import tycoon.ui.{Tile, Renderable, DraggableTiledPane}

import scalafx.beans.property._
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
import scalafx.util.converter.{DateStringConverter, DateTimeStringConverter}


class Game(val map_width : Int, val map_height : Int)
{
  var mapName = Settings.GameTitle + " Map"

  var infoTextTimer: Double = 0
  val informationText = StringProperty("")
  def clearInfoText(force: Boolean = true): Unit = {
    if (force || (!force && infoTextTimer < 0))
      setRandomInfoText()
  }
  def setInfoText(s: String, duration: Double = 4): Unit = {
    informationText.set(s)
    infoTextTimer = duration
  }
  private def setRandomInfoText() = {
    val r = scala.util.Random
    val randomTexts = Dialogues.RandomInfoTexts
    setInfoText(randomTexts(r.nextInt(randomTexts.length)), Dialogues.DefaultInfoTextDuration)
  }
  setRandomInfoText()


  // game map
  var map = new TileMap(map_width, map_height)
  map.fillBackground(Tile.Grass)

  def fillNewGame() {
    map.sprinkleTile(Tile.Tree, 5)
    map.sprinkleTile(Tile.Rock, 1)
    map.generateLakes(4, 200) //SLOW
  }

  val tiledPane = new DraggableTiledPane(map)
  tiledPane.moveToCenter()
  tiledPane.requestFocus()


  // game objects
  var townManager = new TownManager(this)
  var gameGraph = new Graph(townManager)
  var railManager = new RailManager(map, gameGraph)
  var loader = new Loader(this)
  var saver = new Saver(this)

  var nb_structures = 0

  var rails = new ListBuffer[Rail]()
  var structures = new ListBuffer[Structure]()
  var mines = new ListBuffer[Mine]()
  var towns = new ListBuffer[Town]()
  var trains = new ListBuffer[Train]()
  var planes = new ListBuffer[Plane]()
  var trucks = new ListBuffer[Truck]()
  var boats = new ListBuffer[Boat]()
  var routes = new ListBuffer[Route]()
  var trips = new ListBuffer[Trip]()
  var carriages = new ListBuffer[Carriage]()

  private val _player = new Player
  def player: Player = _player

  private val _ai = new AI(this)
  def ai: AI = _ai


  // game loop

  var totalElapsedTime: Double = 0
  var secondsCount: Double = 0
  val elapsedTimeStr = StringProperty("")

  var speedMultiplier = DoubleProperty(1.0)
  var fps = IntegerProperty(0)

  def increaseSpeed() = speedMultiplier.set(Math.min(speedMultiplier.value * 2, 64))
  def decreaseSpeed() = speedMultiplier.set(Math.max(speedMultiplier.value / 2, 0.25))

  private class GameLoop extends AnimationTimer
  {
    var startNanoTime : Long = System.nanoTime()

    def init() = startNanoTime = System.nanoTime()

    override def handle(currentNanoTime: Long) = {
      var elapsedTime : Double = (currentNanoTime - startNanoTime) / 1000000000.0
      startNanoTime = currentNanoTime

      fps.set((1.0 / elapsedTime).toInt)

      update(elapsedTime)
    }
  }

  private val loop = new GameLoop()

  def start () : Unit = {
    // playerMoney = 1000000 // move into player.init()
    loop.init()
    loop.start()
  }
  def pause () : Unit = {}
  def stop () : Unit = {}

  private def update(dt : Double) : Unit = {
    totalElapsedTime += dt * speedMultiplier.value
    secondsCount += dt
    if (secondsCount > 1) {
      player.tick()
      secondsCount -= 1
    }

    var currentDuration: Int = totalElapsedTime.toInt
    val nbYears = currentDuration / 8640
    currentDuration %= 8640
    val nbMonths = currentDuration / 720
    currentDuration %= 720
    val nbDays = currentDuration / 24
    currentDuration %= 24
    val nbHours = currentDuration

    elapsedTimeStr.set(
      (if (nbYears > 0) nbYears.toString + "y" else "")
      + (if (nbMonths > 0) nbMonths.toString + "m" else "")
      + (if (nbDays > 0) nbDays.toString + "d" else "")
      + (if (nbHours > 0) nbHours.toString + "h" else "")
    )

    //update trains position here ?

    routes foreach { _.update(dt * speedMultiplier.value) }
    trips foreach { _.update(dt * speedMultiplier.value) }
    // delete routes that are not active anymore
    routes = routes filter { r: Route => r.active || r.repeated }
    trips = trips filter { t: Trip => t.active || t.repeated }
    structures foreach { _.update(dt * speedMultiplier.value)}

    ai.update(dt * speedMultiplier.value)

    tiledPane.render()

    if (infoTextTimer > 0) {
      infoTextTimer -= dt
      if (infoTextTimer <= 0)
        clearInfoText()
    }
  }








  // functions

  def playerName : StringProperty = _player.name
  def playerName_= (new_name: String) = _player.name = new_name

  def playerMoney : IntegerProperty = _player.money
  def playerMoney_= (new_money: Int) = _player.money = new_money

  def playerFormattedMoney: StringProperty = _player.formattedMoney




  /* KEEEEP BEGIN */

  def createStruct(struct: Structure, tilesAllowed: Array[Tile]): Boolean = {
    var resultBool = false
    if (map.gridContains(struct.gridRect)
        && map.isUnused(struct.gridRect)
        && map.checkBgTiles(struct.gridRect, tilesAllowed)) {
      structures += struct
      struct.getOwner.name.value match {
        case "AI" => map.addEntity(new Flag(struct.gridPos,1))
        case _ => map.addEntity(new Flag(struct.gridPos,0))
      }
      map.addStructure(struct)
      nb_structures += 1
      gameGraph.newStructure(struct)

      struct match {
        case town: Town => {townManager.newTown(town)}
        case mine: Mine => { mines += mine ; townManager.newStructure(mine) }
        case s: Structure => townManager.newStructure(s)
      }
      true
    }
    else {
      struct match {
        case m: Mine => setInfoText("You can create mines only on deposits!", 2)
        case m: Dock => setInfoText("You can create Docks only on water or sand!", 2)
        case _ => ()
      }
      false
    }
  }

  def buyStruct(struct: BuyableStruct, pos: GridLocation, player: Player = _player): Boolean = {
    var bought: Boolean = false
    if (player.money.value >= struct.price) {
      struct.newInstance(pos, nb_structures, townManager, player) match {
        case town: Town => towns += town ; bought = createStruct(town, Tile.Grass)
        case mine: Mine => bought = createStruct(mine, Array(Tile.Rock))
        case farm: Farm => bought = createStruct(farm, Tile.Grass)
        case packingPlant: PackingPlant => bought = createStruct(packingPlant, Tile.Grass)
        case factory: Factory => bought = createStruct(factory, Tile.Grass)
        case windMill: WindMill => bought = createStruct(windMill, Tile.Grass)
        case airport: Airport => {
          //Airport is a Town facility, has to be contained in a town.
          val around = map.getSurroundingStructures(pos,1)
          for (neighbor <- around) {
            neighbor match {
              case town: Town => {
                if (!town.hasAirport && createStruct(airport, Tile.Grass)) {
                  bought = true
                  town.hasAirport = true
                  airport.dependanceTown = Some(town)
                  town.airport = Some(airport)
                  townManager.updatePortWeightings(0)
                }
              }
              case _ => ()
            }
          }
        }
        case dock: Dock => {
          val around = map.getSurroundingStructures(pos, 1)
          for (neighbor <- around) {
            neighbor match {
              case town: Town => {
                if (!town.hasDock && createStruct(dock, Tile.Sand ++ Tile.Water)) {
                  bought = true
                  town.hasDock = true
                  dock.dependanceTown = Some(town)
                  town.dock = Some(dock)
                  townManager.updatePortWeightings(1)
                }
              }
              case _ => ()
            }
          }
        }
        case field: Field => {
          val around = map.getSurroundingStructures(pos, 0)
          for (neighbor <- around) {
            neighbor match {
              case farm: Farm => {
                if ((farm.haOfField < 10) && createStruct(field, Tile.Grass)) {
                  bought = true
                  farm.haOfField += 1
                  farm.fields +=  field
                  farm.productionPerPeriod(1) += 4
                  field.dependanceFarm = Some(farm)
                }
              }
              case fieldBis : Field => {
                if ((fieldBis.dependanceFarm.get.haOfField < 10) && createStruct(field, Tile.Grass)) {
                  bought = true
                  fieldBis.dependanceFarm.get.haOfField += 1
                  fieldBis.dependanceFarm.get.fields += field
                  field.dependanceFarm = fieldBis.dependanceFarm
                }
              }
              case _ => ()
            }
          }
        }
        case _ => ()
      }
    }
    if (bought) player.pay(struct.price, 0)
    bought
  }

  def buyRoad(road: BuyableRoad, pos: GridLocation, player: Player = _player): Boolean = {
    var bought: Boolean = false
    if (player.money.value >= road.price) {
      road.newInstance(pos) match {
        case rail: Rail => bought = railManager.createRail(rail)
        case asphalt: Asphalt => {
          if (map.isUnused(pos) && map.checkBgTile(pos, Tile.Grass)) {
            map.setBackgroundTile(pos, road.tile)
            bought = true
          }
        }
        case grass: Grass => {
          if (map.isUnused(pos) && map.checkBgTile(pos, Array(Tile.Asphalt, Tile.Tree) ++ Tile.Sand ++ Tile.Water)) {
            map.setBackgroundTile(pos, road.tile)
            bought = true
          }
        }
        case water: Water => {
          if (map.isUnused(pos) && map.checkBgTile(pos, Tile.Grass ++ Tile.Sand)) {
            map.setBackgroundTile(pos, road.tile)
            bought = true
          }
        }
        case _ => ()
      }
    }
    if (bought) player.pay(road.price, 1)
    bought
  }

  var nbVehicles: Int = 0

  def buyVehicle(vehicle: BuyableVehicle, pos: GridLocation, player: Player = _player): Boolean = {
    var bought: Boolean = false
    if (player.money.value >= vehicle.price) {
      map.maybeGetStructureAt(pos) match {
        case Some(e) => e match {
          case struct: Structure => {
            vehicle.newInstance(nbVehicles, struct, player) match {
              case train: Train => struct match {
                case town: Town =>
                  createTrain(train, town, player)
                  if (player.name == "AI") {
                    buyPassengerCarriage(train)
                    buyPassengerCarriage(train)
                    buyPassengerCarriage(train)
                  }
                  bought = true
                case _ => ()
              }
              case plane: Plane => struct match {
                case airport: Airport =>
                  createPlane(plane, airport, player)
                  bought = true
                case _ => ()
              }
              case boat: Boat => struct match {
                case dock: Dock =>
                  createBoat(boat, dock, player)
                  bought = true
                case _ => ()
              }
              case truck: Truck => struct match {
                case town: Town =>
                  createTruck(truck, town, player)
                  bought = true
                case _ => ()
              }
              case _ => ()
            }
          }
          case _ => ()
        }
        case None => ()
      }
    }
    if (bought) { nbVehicles += 1 ; player.pay(vehicle.price, 2) }
    bought
  }

  def createRoute(roads: ListBuffer[Road], stops: ListBuffer[Structure], train: Train, repeatRoute: Boolean) = {
    // the constraints of weight and thrust will be added here
    val route = new Route(roads, stops, train, repeatRoute)
    route.start()
    routes += route
  }

  def createTrip(origin: Structure, destination: Structure, veh: Vehicle, repeatTrip: Boolean) = {
    // println("from " + origin.name + " to " + destination.name + " with " + veh + " (repeated: " + repeatTrip + ")")
    val trip = new Trip(origin, destination, veh, repeatTrip)

    var isDijkstra: Boolean = false

    veh match {
      case _: Truck =>
        trip.roadPositions = Dijkstra.tileGraph(origin, destination, Array(Tile.Asphalt), map, 0)
        isDijkstra = true
      case _: Boat =>
        trip.roadPositions = Dijkstra.tileGraph(origin, destination, Tile.Water, map, 1)
        isDijkstra = true
      case _ => ()
    }

    if (trip.roadPositions.nonEmpty || !isDijkstra) {
      trip.start()
      trips += trip
    }
  }

  /* KEEEEP END */


  var nbTrains = IntegerProperty(0)

  def addCarriage(carriage: Carriage, train: Train): Unit = {
    /* TODO add carriage limit constraint */
    map.addEntity(carriage)
    carriages += carriage
    train.addCarriage(carriage)
    nbVehicles += 1
  }
  def buyPassengerCarriage(train: Train): Boolean = {
    if (!train.moving.value && train.owner.pay(Settings.CostPassengerCarriage, 2)) {
      addCarriage(new PassengerCarriage(nbVehicles, train.location, _player), train)
      true
    } else false
  }
  def buyGoodsCarriage(train: Train): Boolean = {
    if (!train.moving.value && train.owner.pay(Settings.CostGoodsCarriage, 2)) {
      addCarriage(new GoodsCarriage(nbVehicles, train.location, _player), train)
      true
    } else false
  }
  def buyTankCar(train: Train): Boolean = {
    if (!train.moving.value && train.owner.pay(Settings.CostTankCar, 2)) {
      addCarriage(new TankCar(nbVehicles, train.location, _player), train)
      true
    } else false
  }

  def createTrain (train: Train, town: Town, player: Player = player): Unit = {
    town.addVehicle(train)
    trains += train
    map.addEntity(train)
    nbTrains.set(nbTrains.value + 1)
  }

  def createPlane (plane: Plane, airport: Airport, player: Player = player): Unit = {
    airport.addVehicle(plane)
    planes += plane
    map.addEntity(plane)
  }

  def createBoat (boat: Boat, port: Dock, player: Player = player): Unit = {
    port.addVehicle(boat)
    boats += boat
    map.addEntity(boat)
  }

  def createTruck (truck: Truck, struct: Structure, player: Player = player): Unit = {
    struct.addVehicle(truck)
    trucks += truck
    map.addEntity(truck)
  }

  def loadMap(filepath: String) : Boolean = {
    Try {
      loader.loadMap(filepath)
    }.isSuccess
  }
}
