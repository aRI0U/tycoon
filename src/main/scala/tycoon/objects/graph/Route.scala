package tycoon.objects.graph

import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.ui.Tile
import tycoon.objects.vehicle.train._
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle._
import tycoon.game.Game
import tycoon.game.GridLocation

class Route(itinerary: ListBuffer[Road], train: Train) {
  private var onTheRoad = true
  private var dirIndicator = 1


  var currentRoad: Option[Road] = None
  val stops: ListBuffer[Structure] = determineStops(itinerary)

  def manageTile(entity : Renderable, direction : Int) = {
    val tileList = new ListBuffer[Tile]()
    tileList += (Tile.passengerWagonB, Tile.passengerWagonR, Tile.passengerWagonT, Tile.passengerWagonL,
                 Tile.goodsWagonB, Tile.goodsWagonR, Tile.goodsWagonT, Tile.goodsWagonL,
                 Tile.locomotiveB, Tile.locomotiveR, Tile.locomotiveT, Tile.locomotiveL)
    var entityType = 1
    entity match {
      case train : Train => entityType = 2
      case p : PassengerCarriage => entityType = 0
      case _ => ;
    }
    entity.tile = tileList(direction + 4 * entityType)
  }

  def determineStops(itinerary: ListBuffer[Road]) : ListBuffer[Structure] = {
    var stops = new ListBuffer[Structure]
    for (road <- itinerary) {
      road.startStructure match {
        case Some(s) => {
          road.endStructure match {
            case Some(e) => {
              stops += s
              stops += e
            }
            case None => println("tycoon > objects > graph > Route : unfinished road (not supposed to happen)")
          }
        }
        case None => println("tycoon > objects > graph > Route : unfinished road (not supposed to happen)")
      }
    }
    stops.distinct
  }

  def departure() = {
    // fill carriages, owner earns money accordingly
    stops -= train.location

    train.boarding(stops)

    carriageMovement(train.location.gridPos, None, train.carriageList)

    if (train.location == itinerary.last.startStructure.get) dirIndicator = 0 // PAS DE GET
    else dirIndicator = 1


    train.location.trainList -= train
    train.visible = true
    train.carriageList foreach (_.visible = true)

    currentRoad = Some(itinerary.last)
    for (rail <- itinerary.last.rails)
      if (rail.nextInDir((dirIndicator + 1) % 2) == rail)
        train.currentRail = Some(rail)

    itinerary -= itinerary.last
    rotateVehicle(train)
    train.gridPos = (train.currentRail.get).gridPos.clone()


  }

  def arrival (road: Road) = {
    if (dirIndicator == 1)
      road.startStructure match {
        case Some(struct) => train.location = struct
        case None => ()
      }
    else
      road.endStructure match {
        case Some(struct) => train.location = struct
        case None => ()
      }


    train.location.trainList += train
    stops -= train.location
    train.landing()

    train.gridPos = train.location.gridPos.right

    if (itinerary.size == 0) {
      onTheRoad = false
      for (carr <- train.carriageList) {
        carr.visible = false
        carr.currentRail = None
      }
    }
    else carriageMovement(train.currentRail.get.gridPos, train.currentRail, train.carriageList)
    train.currentRail = None
    train.visible = true

    if(stops.length > 0)
      departure()
  }

  // train movement
  def updateBox (road: Road, dt: Double) = {
    train.currentRail match {
      case Some(rail) => {
        if (rail.nextInDir(dirIndicator) == rail) {
          if (train.gridPos.percentageHeight > 0) {
            train.gridPos.percentageHeight -= dt * train.speed.value
            if (train.gridPos.percentageHeight <= 0) {
              train.gridPos.percentageHeight = 0
              rotateVehicle(train)
            }
          }
          else if (train.gridPos.percentageWidth > 0) {
            train.gridPos.percentageWidth -= dt * train.speed.value
            if (train.gridPos.percentageWidth <= 0) {
              train.gridPos.percentageHeight = 0
              rotateVehicle(train)
            }
          }
          else
            arrival(road)
        }
        else {
          if (rail.nextInDir(dirIndicator).gridPos.eq(train.gridPos.right)) {
            if (train.gridPos.percentageHeight > 0) {
              train.gridPos.percentageHeight -= dt * train.speed.value
              if (train.gridPos.percentageHeight <= 0) {
                train.gridPos.percentageHeight = 0
                rotateVehicle(train)
              }
            } else {
              train.gridPos.percentageWidth += dt * train.speed.value
              if (train.gridPos.percentageWidth > 100) {
                train.gridPos = train.gridPos.right
                train.gridPos.percentageWidth = 0
                train.currentRail = Some(rail.nextInDir(dirIndicator))
                rotateVehicle(train)
                carriageMovement(rail.gridPos, Some(rail), train.carriageList)
              }
            }
          } else if (rail.nextInDir(dirIndicator).gridPos.eq(train.gridPos.left)) {
            if (train.gridPos.percentageHeight > 0) {
              train.gridPos.percentageHeight -= dt * train.speed.value
              if (train.gridPos.percentageHeight <= 0) {
                train.gridPos.percentageHeight = 0
                rotateVehicle(train)
              }
            } else {
              train.gridPos.percentageWidth -= dt * train.speed.value
              if (train.gridPos.percentageWidth <= 0) {
                train.gridPos = train.gridPos.left
                train.gridPos.percentageWidth = 100
                train.currentRail = Some(rail.nextInDir(dirIndicator))
                carriageMovement(rail.gridPos, Some(rail), train.carriageList)
              }
            }
          } else if (rail.nextInDir(dirIndicator).gridPos.eq(train.gridPos.top)) {
            if (train.gridPos.percentageWidth > 0) {
              train.gridPos.percentageWidth -= dt * train.speed.value
              if (train.gridPos.percentageWidth <= 0) {
                train.gridPos.percentageHeight = 0
                rotateVehicle(train)
              }
            } else {
              train.gridPos.percentageHeight -= dt * train.speed.value
              if (train.gridPos.percentageHeight <= 0) {
                train.gridPos = train.gridPos.top
                train.gridPos.percentageHeight = 100
                train.currentRail = Some(rail.nextInDir(dirIndicator))
                carriageMovement(rail.gridPos, Some(rail), train.carriageList)
              }
            }
          } else if (rail.nextInDir(dirIndicator).gridPos.eq(train.gridPos.bottom)) {
            if (train.gridPos.percentageWidth > 0) {
              train.gridPos.percentageWidth -= dt * train.speed.value
              if (train.gridPos.percentageWidth <= 0) {
                train.gridPos.percentageHeight = 0
                rotateVehicle(train)
              }
            } else {
              train.gridPos.percentageHeight += dt * train.speed.value
              if (train.gridPos.percentageHeight > 100) {
                train.gridPos = train.gridPos.bottom
                train.gridPos.percentageHeight = 0
                train.currentRail = Some(rail.nextInDir(dirIndicator))
                rotateVehicle(train)
                carriageMovement(rail.gridPos, Some(rail), train.carriageList)
              }
            }
          }
        }
      }
      case None => ()
    }
  }


  def update (dt: Double) {
    if (onTheRoad) updateBox(currentRoad.get, dt)
  }



  /// TWO NEXT FUNCTION COULD BE MERGE
// give the tile the orientation fitting with the rail
  def rotateVehicle (thing: Renderable) = {
    var r = new Rail(new GridLocation(-1,-1))
    thing match {
      case t: Train => r = t.currentRail.get
      case c: Carriage => r = c.currentRail.get
    }
    //choosing rail with witch we compare the direction tookeen by the train
    var compRail = r.nextInDir((dirIndicator - 1) %2)
    var plus = 1
    if (!(r == r.nextInDir(dirIndicator))) {
      compRail = r.nextInDir(dirIndicator)
      plus = 0
    }
    var id = 3
    var x = r.gridPos.col - compRail.gridPos.col
    var y = r.gridPos.row - compRail.gridPos.row
    if (x == -1)
      id = 1
    if (y == 1)
      id = 2
    if (x == 1)
      id = 3
    if (y == -1)
      id = 0
    id = ((id + 2 * plus) % 4)
    manageTile(thing, id)
  }

  def wagon_rotation(thing: Carriage) = {
    var r = thing.currentRail.get
    //choosing rail with witch we compare the direction tookeen by the train
    var compRail = r.nextInDir((dirIndicator - 1) %2)
    var plus = 1
    if (!(r == r.nextInDir(dirIndicator))) {
      compRail = r.nextInDir(dirIndicator)
      plus = 0
    }
    var x = r.gridPos.col - compRail.gridPos.col
    var y = r.gridPos.row - compRail.gridPos.row
    if (x == 1)
      thing.tile = Tile.passengerWagonT
    if (x == -1)
      thing.rotation(- 90 + plus * 180)
    if (y == 1)
      thing.rotation(180 + plus * 180)
    if (y == -1)
      thing.rotation(0 + plus * 180)
  }
  protected var internTime: Double = 0

  def carriageMovement(firstPosition: GridLocation, optionRail: Option[Rail], carriages: ListBuffer[Carriage]) = {
    if (!carriages.isEmpty) {
      var pos1 = new GridLocation(firstPosition)
      var pos2 = new GridLocation(firstPosition)
      var optionRail1 = optionRail
      var optionRail2 = optionRail
      for (car <- carriages) {
        pos2 = car.gridPos
        optionRail2 = car.currentRail
        car.gridPos = (pos1)
        car.currentRail = optionRail1
        optionRail1 match {
          case Some(r) => rotateVehicle(car)
          case _ => ()
        }
        pos1 = pos2
        optionRail1 = optionRail2
      }
    }
  }


}
