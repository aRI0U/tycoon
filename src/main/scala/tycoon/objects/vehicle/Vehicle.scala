package tycoon.objects.vehicle

import tycoon.ui.Renderable
import tycoon.objects.railway.Road
import tycoon.objects.structure._
import tycoon.game.{Player, GridLocation}
import scalafx.beans.property._
import tycoon.ui.Tile
import tycoon.game._
import scala.collection.mutable.ListBuffer


abstract class Vehicle(_id: Int, struct: Structure, owner: Player) extends Renderable(new GridLocation(-1, -1)) {
  var weight: Double
  var consumption : Double

  // dynamic values
  var accDistance : Double = 1.0 // determine the necesary distance to travel to get maximal speed

  var decDistance : Double = 1.0// determine the distance to the next stop where the vehicle must brake
  var initialSpeed : Double = 1.0

  def accFunction (d: Double) : Double
  def decFunction (d: Double) : Double

  var defaultBrakeTime : Double = 1.0
  var currentBrakeTime : Double = 0.0

  def brake() = currentBrakeTime = defaultBrakeTime

  def id: Int = _id

  protected var _moving = BooleanProperty(false)
  def moving: BooleanProperty = _moving

  protected var _location: ObjectProperty[Structure] = ObjectProperty(struct)
  protected var _locationName = StringProperty(struct.name)
  _location.onChange {
    println("change location")
    println(locationPos)
    _locationName.set(_location.value.name)
    locationPos = location.gridPos
    println(locationPos)
  }
  var locationPos = location.gridPos

  protected var _nextLocation: ObjectProperty[Option[Structure]] = ObjectProperty(None)
  protected var _nextLocationName = StringProperty("-")
  var nextLocationPos : Option[GridLocation] = None
  _nextLocation.onChange {
    _nextLocation.value match {
      case Some(struct) => {
        println("change nextLocationPos")
        _nextLocationName.set(struct.name)
        println(nextLocationPos)
        nextLocationPos = Some(struct.gridPos)
        println(nextLocationPos)
      }
      case None => {
        _nextLocationName.set("-")
        nextLocationPos = None
      }
    }
  }

  var arrived: Boolean = true
  var stabilized: Boolean = false

  def location: Structure = _location.value
  def location_=(newStruct: Structure) = _location.set(newStruct)
  def nextLocation: Option[Structure] = _nextLocation.value
  def locationName: StringProperty = _locationName
  def nextLocationName: StringProperty = _nextLocationName

  protected var _engine: ObjectProperty[Engine] = ObjectProperty(new Engine(owner, this))
  protected var _engineThrust: DoubleProperty = _engine.value.thrust
  def engineThrust : DoubleProperty = _engineThrust

  def engineUpgradeLevel: IntegerProperty = _engine.value.upgradeLevel
  def upgradeEngine(): Boolean = _engine.value.upgrade()

  protected var _speed = DoubleProperty(0)
  def speed: DoubleProperty = _speed

  // speedLimit is a coefficient between 0 and 1 to implement acceleration and breaking
  protected var _speedLimit = DoubleProperty(1)
  def speedLimit : DoubleProperty = _speedLimit

  def departure() = {
    println("departure")
    owner.setCurrentVehicle(this)
    owner.pay((weight * consumption).toInt, 3)
    arrived = false
    visible = true
    moving.set(true)
    stabilized = false
    speed <== engineThrust*speedLimit
    println("start = " + location)
    println("destination = " + nextLocation)
  }

  def arrival() = {
    println("arrival")
    brake()
    moving.set(false)
    speed <== DoubleProperty(0)
  }

  def boarding(stops: ListBuffer[Structure]) = {
    _nextLocation.set(Some(stops(0)))
  }

  def landing() = {
    _nextLocation.set(None)
  }

  def update(dt: Double, dirIndicator: Int) = {
    if (currentBrakeTime > 0) currentBrakeTime -= dt
    else {
      currentBrakeTime = 0
      if (moving.value) {
        nextLocationPos match {
          case Some(pos) => determineSpeedLimit(this.gridPos, locationPos, pos)
          case None => println("forgot to update nextLocationPos")
        }
      }
    }
  }

  def getDirs(origin: GridLocation, destination: GridLocation): ListBuffer[Direction] = {
    val dirs = new ListBuffer[Direction]()
    if (destination.row < origin.row || (destination.row == origin.row && origin.percentageHeight > 0)) dirs += North
    if (destination.col > origin.col) dirs += East
    if (destination.row > origin.row) dirs += South
    if (destination.col < origin.col || (destination.col == origin.col && origin.percentageWidth > 0)) dirs += West
    dirs
  }

  def stabilize(pos: GridLocation, dt: Double, speed: Double): Boolean = {
    pos.percentageWidth = Math.max(pos.percentageWidth - dt * speed, 0)
    pos.percentageHeight = Math.max(pos.percentageHeight - dt * speed, 0)

    (pos.percentageHeight == 0 && pos.percentageWidth == 0)
  }

  def euclidianDistance(p1: GridLocation, p2: GridLocation) : Double = Math.sqrt(Math.pow(p1.col - p2.col, 2) + Math.pow(p1.row - p2.row, 2))

  def determineSpeedLimit(pos: GridLocation, previousStop: GridLocation, nextStop: GridLocation) = {
    val distancePreviousStop = euclidianDistance(pos, previousStop)
    val distanceNextStop = euclidianDistance(pos, nextStop)
    if (distancePreviousStop < accDistance) {
      if (distanceNextStop < decDistance) {
        speedLimit.set(accFunction(distancePreviousStop/accDistance).min(decFunction(distanceNextStop/decDistance)).max(initialSpeed))
        //println("start + end")
      }
      else {
        speedLimit.set(accFunction(distancePreviousStop/accDistance).max(initialSpeed))
        //println("start")
      }

    }
    else {
      if (distanceNextStop < decDistance) {
        speedLimit.set(decFunction(distanceNextStop/decDistance).max(initialSpeed))
      }
      else speedLimit.set(1.0)
    }
  }

  // returns true iff the move lead to a change of case (ie percentage outbounds 0/100)
  def move(pos: GridLocation, dir: Direction, dt: Double, speed: Double) : Boolean = {
    var changedSquare: Boolean = false
    dir match {
      case North =>
        pos.percentageHeight -= dt * speed
        if (pos.percentageHeight <= 0)
          { pos.setLoc(pos.top) ; pos.percentageHeight = 100 ; changedSquare = true }
      case East =>
        pos.percentageWidth += dt * speed
        if (pos.percentageWidth > 100)
          { pos.percentageWidth = 0 ; pos.setLoc(pos.right) ; changedSquare = true }
      case South =>
        pos.percentageHeight += dt * speed
        if (pos.percentageHeight > 100)
          { pos.percentageHeight = 0 ; pos.setLoc(pos.bottom) ; changedSquare = true }
      case West =>
        pos.percentageWidth -= dt * speed
        if (pos.percentageWidth <= 0)
          { pos.percentageWidth = 100 ; pos.setLoc(pos.left) ; changedSquare = true }
      case Undefined => ()
    }
    changedSquare
  }

  val whyBrake = "Technical problem: necesary to stop for a while"

  def unfortunateEvent() : String = {
    brake()
    locationPos = this.gridPos
    whyBrake
  }
}
