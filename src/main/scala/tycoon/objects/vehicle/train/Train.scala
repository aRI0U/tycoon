package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.game.Game
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle._
import scalafx.beans.property._
import tycoon.ui.Tile
import tycoon.game.{Game, GridLocation, Player}
import tycoon.ui.DraggableTiledPane




class Train(val id: Int, initialTown: Structure, val owner: Player) extends Vehicle(id, initialTown, owner) {

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


  def update(dt: Double, dirIndicator: Int) = {
    move(dt, dirIndicator)
    for (c <- carriageList)
      if (c.visible == true) {
        moveCarriage(c, dt, dirIndicator)
      }
  }

  // train movement
  def move(dt: Double, dirIndicator: Int) = {
    currentRail match {
      case Some(rail) => {
        if (rail.nextInDir((dirIndicator + 1) % 2) == rail) // first rail
          rotateTrain(this, dirIndicator)
        if (rail.nextInDir(dirIndicator) == rail) { // last rail
          if (gridPos.percentageHeight > 0) {
            moveTmp(gridPos, North, dt, speed.value, moveNext = false)
            rotateTrain(this, dirIndicator)
          }
          else if (gridPos.percentageWidth > 0) {
            moveTmp(gridPos, West, dt, speed.value, moveNext = false)
            rotateTrain(this, dirIndicator)
          }
          else
            arrived = true
        }
        else {
          if (rail.nextInDir(dirIndicator).gridPos.eq(gridPos.right)) {
            if (gridPos.percentageHeight > 0) {
              if (moveTmp(gridPos, North, dt, speed.value, moveNext = false)) {
                rotateTrain(this, dirIndicator)
                maybeDisplayNewCarriage()
              }
            } else {
              if (moveTmp(gridPos, East, dt, speed.value)) {
                currentRail = Some(rail.nextInDir(dirIndicator))
                rotateTrain(this, dirIndicator)
                maybeDisplayNewCarriage()
              }
            }
          } else if (rail.nextInDir(dirIndicator).gridPos.eq(gridPos.left)) {
            if (gridPos.percentageHeight > 0) {
              if (moveTmp(gridPos, North, dt, speed.value, moveNext = false)) {
                rotateTrain(this, dirIndicator)
                maybeDisplayNewCarriage()
              }
            } else {
              if (moveTmp(gridPos, West, dt, speed.value)) {
                currentRail = Some(rail.nextInDir(dirIndicator))
                maybeDisplayNewCarriage()
                //carriageMovement(rail.gridPos, Some(rail), carriageList)
              }
            }
          } else if (rail.nextInDir(dirIndicator).gridPos.eq(gridPos.top)) {
            if (gridPos.percentageWidth > 0) {
              if (moveTmp(gridPos, West, dt, speed.value, moveNext = false)) {
                rotateTrain(this, dirIndicator)
                maybeDisplayNewCarriage()
              }
            } else {
              if (moveTmp(gridPos, North, dt, speed.value)) {
                currentRail = Some(rail.nextInDir(dirIndicator))
                maybeDisplayNewCarriage()
                //carriageMovement(rail.gridPos, Some(rail), carriageList)
              }
            }
          } else if (rail.nextInDir(dirIndicator).gridPos.eq(gridPos.bottom)) {
            if (gridPos.percentageWidth > 0) {
              if (moveTmp(gridPos, West, dt, speed.value, moveNext = false)) {
                rotateTrain(this, dirIndicator)
                maybeDisplayNewCarriage()
              }
            } else {
              if (moveTmp(gridPos, South, dt, speed.value)) {
                currentRail = Some(rail.nextInDir(dirIndicator))
                rotateTrain(this, dirIndicator)
                maybeDisplayNewCarriage()
                //carriageMovement(rail.gridPos, Some(rail), carriageList)
              }
            }
          }
        }
      }
      case None => ()
    }
  }

// a ne pas appeler si on va north ou west sur le premier rail (ni si on a un virage en premier rail)
// plus simple regarder qd currentRail du train change et alors afficher un nouveau carriage
// pas suffisant -> regarder si le train est 0/0% ?
  def maybeDisplayNewCarriage() = {
    if (carriageList.filter(_.visible == false).nonEmpty) {
      carriageList.filter(_.visible == false)(0).visible = true
    }
  }

  // train movement
  def moveCarriage(c: Carriage, dt: Double, dirIndicator: Int) = {
    c.currentRail match {
      case Some(rail) => {
        if (rail.nextInDir((dirIndicator + 1) % 2) == rail) // first rail
          {}//rotateTrain(c, dirIndicator)
        if (rail.nextInDir(dirIndicator) == rail) { // last rail
          if (c.gridPos.percentageHeight > 0) {
            moveTmp(c.gridPos, North, dt, speed.value, moveNext = false)
            //rotateTrain(c, dirIndicator)
          }
          else if (c.gridPos.percentageWidth > 0) {
            moveTmp(c.gridPos, West, dt, speed.value, moveNext = false)
            //rotateTrain(c, dirIndicator)
          }
          else
            arrived = true
        }
        else {
          if (rail.nextInDir(dirIndicator).gridPos.eq(c.gridPos.right)) {
            if (c.gridPos.percentageHeight > 0) {
              if (moveTmp(c.gridPos, North, dt, speed.value, moveNext = false))
                {}//rotateTrain(c, dirIndicator)
            } else {
              if (moveTmp(c.gridPos, East, dt, speed.value)) {
                c.currentRail = Some(rail.nextInDir(dirIndicator))
                //rotateTrain(c, dirIndicator)
              }
            }
          } else if (rail.nextInDir(dirIndicator).gridPos.eq(c.gridPos.left)) {
            if (c.gridPos.percentageHeight > 0) {
              if (moveTmp(c.gridPos, North, dt, speed.value, moveNext = false))
                {}//rotateTrain(c, dirIndicator)
            } else {
              if (moveTmp(c.gridPos, West, dt, speed.value)) {
                c.currentRail = Some(rail.nextInDir(dirIndicator))
                //carriageMovement(rail.gridPos, Some(rail), carriageList)
              }
            }
          } else if (rail.nextInDir(dirIndicator).gridPos.eq(c.gridPos.top)) {
            if (c.gridPos.percentageWidth > 0) {
              if (moveTmp(c.gridPos, West, dt, speed.value, moveNext = false))
                {}//rotateTrain(c, dirIndicator)
            } else {
              if (moveTmp(c.gridPos, North, dt, speed.value)) {
                c.currentRail = Some(rail.nextInDir(dirIndicator))
                //carriageMovement(rail.gridPos, Some(rail), carriageList)
              }
            }
          } else if (rail.nextInDir(dirIndicator).gridPos.eq(c.gridPos.bottom)) {
            if (c.gridPos.percentageWidth > 0) {
              if (moveTmp(c.gridPos, West, dt, speed.value, moveNext = false))
                {}//rotateTrain(c, dirIndicator)
            } else {
              if (moveTmp(c.gridPos, South, dt, speed.value)) {
                c.currentRail = Some(rail.nextInDir(dirIndicator))
                //rotateTrain(c, dirIndicator)
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

  def rotateTrain(v: Vehicle, dirIndicator: Int) = {
    v match {
      case train: Train => {
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
      case _ => ()
    }
  }

  tile = Tile.locomotiveT
  var weight = 50
  val cost = 200
  var currentRail : Option[Rail] = None
  var carriageList = new ListBuffer[Carriage]()
  var from = StringProperty(initialTown.name)

  addCarriages()

  gridPos = location.gridPos.right
  carriageList foreach (_.visible = false)
  carriageList foreach (_.gridPos = location.gridPos.right)

  def addCarriages() {
    // tmp
    // il faut ajouter les carriage dans game et les donner au train (pour l'id)
    carriageList += new PassengerCarriage(0, initialTown, owner)
    carriageList += new PassengerCarriage(0, initialTown, owner)
    carriageList += new PassengerCarriage(0, initialTown, owner)
    carriageList += new GoodsCarriage(0, initialTown, owner)
    carriageList += new GoodsCarriage(0, initialTown, owner)
  }

  def departure(firstRail: Rail) = {
    currentRail = Some(firstRail)
    gridPos = firstRail.gridPos.clone()
    arrived = false
    visible = true
    moving.set(true)

    for (carr <- carriageList) {
      carr.currentRail = Some(firstRail)
      carr.gridPos = firstRail.gridPos.clone()
      carr.visible = false
    }

    speed.set(engineThrust.value) // modulo weight of carriages here..
  }

  def arrival() = {
    for (carr <- carriageList) {
      carr.visible = false
      carr.currentRail = None
    }
    moving.set(false)
    speed.set(0)
    _nextLocation.set(None)
  }

  def boarding(stops: ListBuffer[Structure]) = {
    carriageList foreach (_.embark(location, stops))
    _nextLocation.set(Some(stops(0)))
  }

  def landing() = {
    carriageList.foreach(_.debark(location))
  }
}
