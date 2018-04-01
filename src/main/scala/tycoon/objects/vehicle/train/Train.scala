package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.game.Game
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle.Vehicle
import scalafx.beans.property._
import tycoon.ui.Tile
import tycoon.game.{Game, GridLocation, Player}
import tycoon.ui.DraggableTiledPane


class Train(val id: Int, initialTown: Town, val nbCarriages: IntegerProperty, val owner: Player) extends Vehicle(initialTown) {

  // used in display
  // id
  private var _moving = BooleanProperty(false)
  def moving: BooleanProperty = _moving

  // current structure if moving is false, origin structure otherwise
  private var _location: ObjectProperty[Structure] = ObjectProperty(initialTown)
  private var _locationName = StringProperty(initialTown.name)
  _location.onChange { _locationName.set(_location.value.name) }

  private var _nextLocation: ObjectProperty[Option[Structure]] = ObjectProperty(None)
  private var _nextLocationName = StringProperty("-")
  _nextLocation.onChange {
    _nextLocation.value match {
      case Some(struct) => _nextLocationName.set(struct.name)
      case None => _nextLocationName.set("-")
    }
  }

  def location: Structure = _location.value
  def location_=(newStruct: Structure) = _location.set(newStruct)
  def locationName: StringProperty = _locationName
  def nextLocationName: StringProperty = _nextLocationName

  private var _engine: ObjectProperty[Engine] = ObjectProperty(new BasicEngine(owner))
  private var _engineThrust: DoubleProperty = _engine.value.thrust
  private var _speed = DoubleProperty(0)
  def speed: DoubleProperty = _speed
  def engineThrust: DoubleProperty = _engineThrust

  var arrived: Boolean = true


  // train movement
  def move(dt: Double, dirIndicator: Int) = {
    currentRail match {
      case Some(rail) => {
        if (rail.nextInDir(dirIndicator) == rail) {
          if (gridPos.percentageHeight > 0) {
            gridPos.percentageHeight -= dt * speed.value
            if (gridPos.percentageHeight <= 0) {
              gridPos.percentageHeight = 0
              rotateTrain(this, dirIndicator)
            }
          }
          else if (gridPos.percentageWidth > 0) {
            gridPos.percentageWidth -= dt * speed.value
            if (gridPos.percentageWidth <= 0) {
              gridPos.percentageHeight = 0
              rotateTrain(this, dirIndicator)
            }
          }
          else
            arrived = true
        }
        else {
          if (rail.nextInDir(dirIndicator).gridPos.eq(gridPos.right)) {
            if (gridPos.percentageHeight > 0) {
              gridPos.percentageHeight -= dt * speed.value
              if (gridPos.percentageHeight <= 0) {
                gridPos.percentageHeight = 0
                rotateTrain(this, dirIndicator)
              }
            } else {
              gridPos.percentageWidth += dt * speed.value
              if (gridPos.percentageWidth > 100) {
                gridPos = gridPos.right
                gridPos.percentageWidth = 0
                currentRail = Some(rail.nextInDir(dirIndicator))
                rotateTrain(this, dirIndicator)
                //carriageMovement(rail.gridPos, Some(rail), carriageList)
              }
            }
          } else if (rail.nextInDir(dirIndicator).gridPos.eq(gridPos.left)) {
            if (gridPos.percentageHeight > 0) {
              gridPos.percentageHeight -= dt * speed.value
              if (gridPos.percentageHeight <= 0) {
                gridPos.percentageHeight = 0
                rotateTrain(this, dirIndicator)
              }
            } else {
              gridPos.percentageWidth -= dt * speed.value
              if (gridPos.percentageWidth <= 0) {
                gridPos = gridPos.left
                gridPos.percentageWidth = 100
                currentRail = Some(rail.nextInDir(dirIndicator))
                //carriageMovement(rail.gridPos, Some(rail), carriageList)
              }
            }
          } else if (rail.nextInDir(dirIndicator).gridPos.eq(gridPos.top)) {
            if (gridPos.percentageWidth > 0) {
              gridPos.percentageWidth -= dt * speed.value
              if (gridPos.percentageWidth <= 0) {
                gridPos.percentageHeight = 0
                rotateTrain(this, dirIndicator)
              }
            } else {
              gridPos.percentageHeight -= dt * speed.value
              if (gridPos.percentageHeight <= 0) {
                gridPos = gridPos.top
                gridPos.percentageHeight = 100
                currentRail = Some(rail.nextInDir(dirIndicator))
                //carriageMovement(rail.gridPos, Some(rail), carriageList)
              }
            }
          } else if (rail.nextInDir(dirIndicator).gridPos.eq(gridPos.bottom)) {
            if (gridPos.percentageWidth > 0) {
              gridPos.percentageWidth -= dt * speed.value
              if (gridPos.percentageWidth <= 0) {
                gridPos.percentageHeight = 0
                rotateTrain(this, dirIndicator)
              }
            } else {
              gridPos.percentageHeight += dt * speed.value
              if (gridPos.percentageHeight > 100) {
                gridPos = gridPos.bottom
                gridPos.percentageHeight = 0
                currentRail = Some(rail.nextInDir(dirIndicator))
                rotateTrain(this, dirIndicator)
                //carriageMovement(rail.gridPos, Some(rail), carriageList)
              }
            }
          }
        }
      }
      case None => ()
    }
  }
/*
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
  }*/

  def rotateTrain(train: Train, dirIndicator: Int) = {
    train.currentRail match {
      case Some(rail) => {
        var changeDir = if (rail == rail.nextInDir(dirIndicator)) 1 else 0
        var nextRail = rail.nextInDir((dirIndicator + changeDir) % 2)

        var direction = 1
        if (nextRail.gridPos.eq(rail.gridPos.top)) direction = 0
        else if (nextRail.gridPos.eq(rail.gridPos.right)) direction = 1
        else if (nextRail.gridPos.eq(rail.gridPos.bottom)) direction = 2
        else if (nextRail.gridPos.eq(rail.gridPos.left)) direction = 3
        direction = (direction + 2 * changeDir) % 4

        val tiles = Array(Tile.locomotiveT, Tile.locomotiveR, Tile.locomotiveB, Tile.locomotiveL)
        train.tile = tiles(direction)
      }
      case None => ()
    }
  }
/*
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
  }*/
      //carriageMovement(location.gridPos, None, carriageList)


  //   rotateVehicle(train)
/*carriageMovement(currentRail.get.gridPos, currentRail, carriageList)
currentRail = None
visible = true*/


  tile = Tile.locomotiveT
  val weight = 50
  val cost = 200
  var currentRail : Option[Rail] = None
  var carriageList = new ListBuffer[Carriage]()
  var from = StringProperty(initialTown.name)

  addCarriages()

  gridPos = location match {
    case town: Town => town.gridPos.right
    case struct: Structure => struct.gridPos
  }

  def addCarriages() {
    for (i <- 1 to nbCarriages.value) carriageList += new PassengerCarriage(owner)
    carriageList += new GoodsCarriage(owner)
  }

  def boarding(stops: ListBuffer[Structure]) = {
    moving.set(true)
    arrived = false
    speed.set(engineThrust.value) // modulo weight of carriages here..
    _nextLocation.set(Some(stops(0)))
    carriageList foreach (_.embark(location, stops))
  }

  def landing() = {
    moving.set(false)
    speed.set(0)
    _nextLocation.set(None)
    carriageList.foreach(_.debark(location))
  }
}
