package tycoon.game

import tycoon.game._
import tycoon.ui.Tile
import tycoon.objects.vehicle._
import tycoon.objects.vehicle.train._
import tycoon.objects.railway._
import tycoon.objects.structure._

import scala.xml.XML
import scala.collection.mutable.ListBuffer

class Loader (game : Game) {
  def loadMap(filepath: String) {
    val map = game.map
    val railManager = game.railManager
    val townManager = game.townManager
    game.player.money_=(Int.MaxValue)
    var player = game.player
    val xml = XML.loadFile(filepath)
    //map treatment
    val mapXML = (xml \ "Map")
    // mapName = ((mapXML \ "@name").text)
    val mapName = (mapXML \ "@name").text
    val width = (mapXML \ "@width").text.toInt
    val height = (mapXML \ "@height").text.toInt

    //cities and facilities
    var cities = ListBuffer[Array[Any]]()
    var id = 0
    val r = scala.util.Random
    // Ground Tiles
    for (tile <- (mapXML \\ "Tile")) {
      // map.setBackgroundTile(((tile \ "@x").text.toInt),((tile \ "@y").text.toInt),showRaw((tile \ "@type").text))
      (tile \ "@type").text match {
        case "water" => map.setBackgroundTile(((tile \ "@x").text.toInt),((tile \ "@y").text.toInt),Tile.Water(r.nextInt(Tile.Water.length)))
        case "grass" => map.setBackgroundTile(((tile \ "@x").text.toInt),((tile \ "@y").text.toInt),Tile.Grass(r.nextInt(Tile.Grass.length)))
        case "sand" => map.setBackgroundTile(((tile \ "@x").text.toInt),((tile \ "@y").text.toInt),Tile.Sand(r.nextInt(Tile.Sand.length)))
        case "asphalt" => map.setBackgroundTile(((tile \ "@x").text.toInt),((tile \ "@y").text.toInt),Tile.Asphalt)
        case "tree" => map.setBackgroundTile(((tile \ "@x").text.toInt),((tile \ "@y").text.toInt),Tile.Tree)
        case "rock" => map.setBackgroundTile(((tile \ "@x").text.toInt),((tile \ "@y").text.toInt),Tile.Rock)
      }
    }
    for (struct <- (mapXML \\ "Structure")) {
      var owner = player
      (struct \ "@owner").text match {
        case "AI" => owner = game.ai
        case _ => ()
      }
      (struct \ "@type").text match {
        case "town" => {
          var pos = new GridLocation((struct \ "@x").text.toInt,(struct \ "@y").text.toInt)
          var town : Town = new SmallTown(pos,id,townManager,owner)
          if ((struct \ "@typebis").text.toInt == 10000 ) town = new MediumTown(pos,id,townManager,owner)
          else if ((struct \ "@typebis").text.toInt == 100000 ) town = new LargeTown(pos,id,townManager,owner)
          if (game.createStruct(town,Tile.Grass)) id+=1
          town.setName((struct \ "@name").text)
          town.population_=((struct \ "@population").text.toInt)
        }
        case "factory" => {
          var pos = new GridLocation((struct \ "@x").text.toInt,(struct \ "@y").text.toInt)
          var factory = new Factory(pos,id,townManager,owner)
          if (game.createStruct(factory,Tile.Grass)) id+=1
          factory.workers_=((struct \ "@population").text.toInt)
          factory.setName((struct \ "@name").text)
        }
        case "farm" => {
          var pos = new GridLocation((struct \ "@x").text.toInt,(struct \ "@y").text.toInt)
          var farm = new Farm(pos,id,townManager,owner)
          if (game.createStruct(farm,Tile.Grass)) id+=1
          farm.workers_=((struct \ "@population").text.toInt)
          farm.setName((struct \ "@name").text)
        }
        case "mine" => {
          var pos = new GridLocation((struct \ "@x").text.toInt,(struct \ "@y").text.toInt)
          var mine = new Mine(pos,id,townManager,owner)
          if (game.createStruct(mine,Array(Tile.Rock))) id+=1
          mine.workers_=((struct \ "@population").text.toInt)
          mine.setName((struct \ "@name").text)
        }
        case "airport" => {
          var pos = new GridLocation((struct \ "@x").text.toInt,(struct \ "@y").text.toInt)
          var airport = new Airport(pos,id,owner)
          if (game.createStruct(airport,Tile.Grass)) id+=1
          airport.setName((struct \ "@name").text)
        }
        case "dock" => {
          var pos = new GridLocation((struct \ "@x").text.toInt,(struct \ "@y").text.toInt)
          var dock = new Dock(pos,id,owner)
          if (game.createStruct(dock,Tile.Sand ++ Tile.Water)) id+=1
          dock.setName((struct \ "@name").text)
        }
        case "windmill" => {
          var pos = new GridLocation((struct \ "@x").text.toInt,(struct \ "@y").text.toInt)
          var windmill = new WindMill(pos,id,townManager,owner)
          if (game.createStruct(windmill,Tile.Grass)) id+=1
          windmill.setName((struct \ "@name").text)
        }
        case "paking" => {
          var pos = new GridLocation((struct \ "@x").text.toInt,(struct \ "@y").text.toInt)
          var paking = new PackingPlant(pos,id,townManager,owner)
          if (game.createStruct(paking,Tile.Grass)) id+=1
          paking.workers_=((struct \ "@population").text.toInt)
          paking.setName((struct \ "@name").text)
        }
        case _ => {}
      }
    }
    for (road <- (mapXML \\ "Road")) {
      for (railx <- (road \\ "Rail")) {
        var pos = new GridLocation((railx \ "@x").text.toInt,(railx \ "@y").text.toInt)
        var rail = new Rail(pos)
          (map.maybeGetStructureAt((road \ "@beginx").text.toInt,(road \"@beginy").text.toInt),map.maybeGetStructureAt((road \ "@endx").text.toInt,(road \"@endy").text.toInt)) match {
          case (Some(ss : Structure),Some(s : Structure)) => railManager.createRail(rail,target = Some(ss.structureId,s.structureId))
          case _ => railManager.createRail(rail)
        }
      }
    }
    id = 0
    for (trainx <- (mapXML \\ "Train")) {
      map.maybeGetStructureAt((trainx \ "@locationx").text.toInt,(trainx \"@locationy").text.toInt) match {
        case Some(s : Town) => {
          var train = new Train(id,s,game.player)
          game.createTrain(train, s) ; id += 1
          for (car <- (trainx \\ "TankCar")) game.buyTankCar(train)
          for (car <- (trainx \\ "GoodsCarriage")) game.buyGoodsCarriage(train)
          for (car <- (trainx \\ "PassengerCarriage")) game.buyPassengerCarriage(train)
          for (i <- 1 to (trainx \ "@enginelevel").text.toInt) train.upgradeEngine()
        }
        case _ => ()
      }
    }
    for (plane <- (mapXML \\ "Plane")) {
      map.maybeGetStructureAt((plane \ "@locationx").text.toInt,(plane \"@locationy").text.toInt) match {
        case Some(a : Airport) => game.createPlane(new Plane(id,a,game.player),a) ; id+=1
        case _ => ()
      }
    }
    for (boat <- (mapXML \\ "Boat")) {
      map.maybeGetStructureAt((boat \ "@locationx").text.toInt,(boat \"@locationy").text.toInt) match {
        case Some(a : Dock) => game.createBoat(new Boat(id,a,game.player),a) ; id+=1
        case _ => ()
      }
    }
    for (truck <- (mapXML \\ "Truck")) {
      map.maybeGetStructureAt((truck \ "@locationx").text.toInt,(truck \"@locationy").text.toInt) match {
        case Some(t : Town) => game.createTruck(new Truck(id,t,game.player),t) ; id+=1
        case _ => ()
      }
    }

    var playerName = (mapXML \\ "Player" \ "@name").text
    var playerMoney = (mapXML \\ "Player" \ "@money").text.toInt
    var playerTime = (mapXML \\ "Player" \ "@time").text.toFloat
    game.totalElapsedTime = playerTime
    game.player.money_=(playerMoney)
    game.player.name_=(playerName)







    // Upload from xml map given by teacher
    for (city <- (mapXML \\ "City")) {
      var nbFactories = 0
      for (factory <- (city \\ "Factory")) nbFactories += 1
      var pos = new GridLocation((city \ "@x").text.toInt % map.width,(city \ "@y").text.toInt % map.height)
      var town = new LargeTown(pos,id,townManager,player) ; id+=1
      game.createStruct(town,Tile.Grass)
      town.setName((city \ "@name").text)
      town.population_=((city \ "@population").text.toInt)
      if (nbFactories>0) {
        var factory = new Factory(pos.left,id,townManager,player); id+=1
        game.createStruct(factory,Tile.Grass)
        factory.workers_=(nbFactories*100)
        railManager.createRail(new Rail(pos.left.bottom))
        railManager.createRail(new Rail(pos.bottom))

      }
      ( city \\ "Airport") foreach (i => {game.createStruct(new Airport(pos.left.top,id,player),Tile.Grass)/*; id+=1*/ ; town.hasAirport = true })
    }

    // map.sprinkleTile(Tile.Tree, 5)
    // map.sprinkleTile(Tile.Rock, 1)
    for (connection <- (mapXML \\ "Connection")){
      var upstream = (connection  \ "@upstream").text
      var downstream = (connection  \ "@downstream").text
      var town1 = townManager.townsList(0)
      var town2 = townManager.townsList(1)
      for (town <- townManager.townsList){
        if (town.name == upstream) {
          town1 = town
        }
        if (town.name == downstream) {
          town2 = town
        }
      }
      var is = false
      (connection \\ "Rail") foreach (i => is = true)
      if (is) {
        val path = Dijkstra.tileGraph(town1,town2,(Tile.Grass),map,0)
        for (pos <- path) {
          //in order to counter some priority cases with the factories
          if (pos == town1.gridPos.left.left.top || pos == town1.gridPos.left.left.bottom ) {
            railManager.createRail(new Rail(town1.gridPos.left.left))
          }
          if (pos == town2.gridPos.left.left.bottom || pos == town2.gridPos.left.left.top) {
            railManager.createRail(new Rail(town2.gridPos.left.left))
          }
          var rail = new Rail(pos)
          railManager.createRail(rail)
        }
      }
      is = false
      (connection \\ "Road") foreach (i => is = true)
      if (is) {
        val path = Dijkstra.tileGraph(town1,town2,(Tile.Grass ++ Array(Tile.Asphalt)),map,0)
        for (pos <- path) {
          if (!(pos == town1.gridRect.pos || pos == town2.gridRect.pos))
            map.setBackgroundTile(pos,Tile.Asphalt)
        }
      }
      is = false
      (connection \\ "Canal") foreach (i => is = true)
      if (is) {
        val path = Dijkstra.tileGraph(town1,town2,(Tile.Grass ++ Tile.Water),map,0)
        for (pos <- path) {
          if (!(pos == town1.gridRect.pos || pos == town2.gridRect.pos))
            map.setBackgroundTile(pos,Tile.Water(0))
        }
      }
    }
  }
}
// map.generateLakes(5, 2000) //SLOW
