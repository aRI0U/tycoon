package tycoon.game

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.objects.vehicle.train._
import tycoon.objects.vehicle._
import tycoon.objects.graph._
import tycoon.game._
import tycoon.ui.Tile
import scalafx.beans.property._
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
import scalafx.util.converter.{DateStringConverter, DateTimeStringConverter}




class Game(val map_width : Int, val map_height : Int)
{

  // def loadMap(filepath: String) : Boolean = {
  //   Try {
  //     val xml = XML.loadFile(filepath)
  //
  //     //please do somwhere else than
  //     println("All foods:")
  //     val goods = xml \ "Goods"
  //     ( xml \\ "Food" \\ "@name") foreach (i => println(i.text))
  //     ( goods \\ "Ore" \\ "@name") foreach (i => println(i.text))
  //
  //     //map treatment
  //     val mapXML = xml \ "Map"
  //     val mapName = mapXML \ "@name"
  //     val width = mapXML \ "@width"
  //     val height = mapXML \ "@height"
  //
  //     var cities = ListBuffer[Array[Any]]()
  //     for (city <- (mapXML \\ "City")) {
  //       var nbFactories = 0
  //       for (factory <- (city \\ "Factory")) nbFactories+=1
  //       buyStruct(new LargeTown(new GridLocation(city(1),city(2))), new GridLocation(city(1),city(2)),player)
  //       // cities += Array (
  //       //   city \ "@name",
  //       //   city \ "@x",
  //       //   city \ "@y",
  //       //   city \ "@population",
  //       //   nbFactories
  //       // )
  //     }

      // for (city <-  cities){
      //   buyStruct(new LargeTown, new GridLocation(city(1),city(2)),player)
      // }



      // println("\nSpecials:")
      // for (food <- (xml \\ "Food"))
      //   if ((food \ "special").length > 0)
      //     println((food \ "special" \ "@id").text)
      // println("\nSpecials again:")
      // for (id <- (xml \ "food" \ "special"))
      //   println((id \ "@id").text)

  //   }.isSuccess
  // }
  // var playerInit = new Player
  // playerInit.money.set(Int.MaxValue)

  var mapName = "Tycoon Game random map"

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
    val randomTexts = Seq(
      "welcome to the " + mapName,
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
    setInfoText(randomTexts(r.nextInt(randomTexts.length)), 10.0)
  }
  setRandomInfoText()


  // game map
  var game_graph = new Graph
  var map = new TileMap(map_width, map_height)
  map.fillBackground(Tile.grass)

  def fillNewGame() {
    map.sprinkleTile(Tile.tree, 3)
    map.sprinkleTile(Tile.rock, 1)
    map.generateLakes(5, 2000) //SLOW
  }

  val tiledPane = new DraggableTiledPane(map)
  tiledPane.moveToCenter()
  tiledPane.requestFocus()


  // game objects
  var railManager = new RailManager(map, game_graph)
  var townManager = new TownManager(this)

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
  def player: Player = _player


  // game loop

  var totalElapsedTime: Double = 0
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
    playerMoney = 1000000 // move into player.init()
    loop.init()
    loop.start()
  }
  def pause () : Unit = {}
  def stop () : Unit = {}

  private def update(dt : Double) : Unit = {
    totalElapsedTime += dt * speedMultiplier.value

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
    // delete routes that are not active anymore
    routes = routes filter { r: Route => r.active || r.repeated }
    try {
      structures foreach { _.update(dt * speedMultiplier.value)}
    } catch {
      case e: EventException => setInfoText(e.s, 3)
    }

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
        case _ => townManager.newStructure(_)
      }
      true
    }
    else {
      struct match {
        case m: Mine => setInfoText("You can create mines only on deposits!", 2)
        case _ => ()
      }
      false
    }
  }

  def buyStruct(struct: BuyableStruct, pos: GridLocation, player: Player = _player): Boolean = {
    var bought: Boolean = false
    if (player.money.value >= struct.price) {
      struct.newInstance(pos, nb_structures, townManager) match {
        case town: Town => bought = createStruct(town, Tile.grass)
        case mine: Mine => bought = createStruct(mine, Array(Tile.rock))
        case farm: Farm => bought = createStruct(farm, Tile.grass)
        case factory: Factory => bought = createStruct(factory, Tile.grass)
        case packingPlant: PackingPlant => bought = createStruct(packingPlant, Tile.grass)
        case airport: Airport => {
          //Airport is a Town facilitie, has to be contained in a town.
          val around = map.getSurroundingStructures(pos,1)
          for (neighbor <- around) {
            neighbor match {
              case town: Town => {
                if (!town.hasAirport && createStruct(airport, Tile.grass)) {
                  bought = true
                  town.hasAirport = true
                  airport.dependanceTown = Some(town)
                  town.airport = Some(airport)
                }
              }
              case _ => ()
            }
          }
        }
        case dock: Dock => {
          val around = map.getSurroundingStructures(pos,1)
          for (neighbor <- around) {
            neighbor match {
              case town: Town => {
                if (!town.hasDock && createStruct(dock, Tile.sand ++ Tile.water)) {
                  bought = true
                  town.hasDock = true
                  dock.dependanceTown = Some(town)
                  town.dock = Some(dock)
                }
              }
              case _ => ()
            }
          }
        }
        case field: Field => {
          val around = map.getSurroundingStructures(pos,0)
          for (neighbor <- around) {
            neighbor match {
              case farm: Farm => {
                if ((farm.haOfField < 10) && createStruct(field, Tile.grass)) {
                  bought = true
                  farm.haOfField += 1
                  farm.fields +=  field
                  farm.productionPerPeriod(1) += 4
                  field.dependanceFarm = Some(farm)
                }
              }
              case fieldBis : Field => {
                if ((fieldBis.dependanceFarm.get.haOfField < 10) && createStruct(field, Tile.grass)) {
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
    if (bought) player.pay(struct.price)
    bought
  }

  def buyRail(rail: BuyableRoad, pos: GridLocation, player: Player = _player): Boolean = {
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
                  bought = true
                case _ => ()
              }
              case plane: Plane => struct match {
                case airport: Airport =>
                  createPlane(plane, airport, player)
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
    if (bought) { nbVehicles += 1 ; player.pay(vehicle.price) }
    bought
  }

  def createRoute(roads: ListBuffer[Road], stops: ListBuffer[Structure], train: Train, repeatRoute: Boolean) = {
    // the constraints of weight and thrust will be added here
    val route = new Route(roads, stops, train, repeatRoute)
    route.start()
    routes += route
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
    if (!train.moving.value && player.pay(PassengerCarriage.Price)) {
      addCarriage(new PassengerCarriage(nbVehicles, train.location, _player), train)
      true
    } else false
  }
  def buyGoodsCarriage(train: Train): Boolean = {
    if (!train.moving.value && player.pay(GoodsCarriage.Price)) {
      addCarriage(new GoodsCarriage(nbVehicles, train.location, _player), train)
      true
    } else false
  }

  def createTrain (train: Train, town: Town, player: Player): Unit = {
    town.addTrain(train)
    trains += train
    map.addEntity(train)
    nbTrains.set(nbTrains.value + 1)

    for (carriage <- train.carriageList) { // TEMP, todo: interface pour créer train avec carriages et faire payer avant dans buyVehicle
      playerMoney.set(playerMoney.value - carriage.cost)
      map.addEntity(carriage)
      carriages += carriage
    }
  }

  def createPlane (plane: Plane, airport: Airport, player: Player): Unit = {
    airport.addPlane(plane)
    planes += plane
    map.addEntity(plane)
  }

  def createFly (departure: Structure, arrival: Structure, plane : Plane) {
    println ("tycoon > game > Game.scala > create Fly: creation of a fly betwenn to to airport with a plane ")
  }

  def loadMap(filepath: String) : Boolean = {
    Try {
      val xml = XML.loadFile(filepath)

      //please do somwhere else than
      println("All foods:")
      val goods = xml \ "Goods"
      ( xml \\ "Food" \\ "@name") foreach (i => println(i.text))
      ( goods \\ "Ore" \\ "@name") foreach (i => println(i.text))

      //map treatment
      val mapXML = (xml \ "Map")
      // mapName = ((mapXML \ "@name").text)
      val mapName = (mapXML \ "@name").text
      val width = (mapXML \ "@width").text.toInt
      val height = (mapXML \ "@height").text.toInt

      //cities and facilities
      var cities = ListBuffer[Array[Any]]()
      var id = 0
      for (city <- (mapXML \\ "City")) {
        var nbFactories = 0
        for (factory <- (city \\ "Factory")) nbFactories+=1
        var pos = new GridLocation((city \ "@x").text.toInt % map.width,(city \ "@y").text.toInt % map.height)
        var town = new LargeTown(pos,id,townManager) ; id+=1
        createStruct(town,Tile.grass)
        town.setName((city \ "@name").text)
        town.population_=((city \ "@population").text.toInt)
        if (nbFactories>0) {
          var factory = new Factory(pos.left,id,townManager); id+=1
          createStruct(factory,Tile.grass)
          factory.workers_=(nbFactories)
        }
        ( city \\ "Airport") foreach (i => {createStruct(new Airport(pos.left.top,id),Tile.grass); id+=1})
      }

      map.sprinkleTile(Tile.tree, 3)
      map.sprinkleTile(Tile.rock, 1)
      for (connection <- (mapXML \\ "Connection")){
        var upstream = (connection  \ "@upstream").text
        var downstream = (connection  \ "@downstream").text
        println("names of towns", upstream,downstream)
        var town1 = townManager.towns_list(0)
        var town2 = townManager.towns_list(1)
        for (town <- townManager.towns_list){
          // println(town.name)
          if (town.name == upstream) {
            town1 = town
            // println("trouvé un town",town.name,upstream)
          }
          if (town.name == downstream) {
            town2 = town
          }
        }
        var done = false
        (connection /*\\ "Rail"*/) foreach (i => {
          if (!done) {
            val path = Dijkstra.tileGraph(town1,town2,(Tile.grass),map)
            for (pos <- path) {
              var rail = new Rail(pos)
              railManager.createRail(rail)
            }
            if (path.size >0){
              done = true
            }
          }
        })
        done = false
        // (connection \\ "Road") foreach (i => {
        //   if (!done) {
        //     val path = Dijkstra.tileGraph(town1,town2,(Tile.grass ++ Tile.sand),map)
        //     for (pos <- path) {
        //       map.setBackgroundTile(pos,Tile.sand(0))
        //     }
        //     if (path.size >0){
        //       done = true
        //     }
        //   }
        // })
        // done = false
        // (connection \\ "Canal") foreach (i => {
        //   if (!done) {
        //     val path = Dijkstra.tileGraph(town1,town2,(Tile.grass ++ Tile.water),map)
        //     for (pos <- path) {
        //       map.setBackgroundTile(pos,Tile.water(0))
        //     }
        //     if (path.size >0){
        //       done = true
        //     }
        //   }
        // })
      }
      // map.generateLakes(5, 2000) //SLOW
    }
  }.isSuccess
}
