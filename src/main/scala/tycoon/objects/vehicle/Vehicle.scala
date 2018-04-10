package tycoon.objects.vehicle

import tycoon.ui.Renderable
import tycoon.objects.railway.Road
import tycoon.objects.structure._
import tycoon.game.{Player, GridLocation}
import scalafx.beans.property._
import tycoon.ui.Tile
import scala.collection.mutable.ListBuffer




sealed abstract class Direction
case object North extends Direction
case object East extends Direction
case object South extends Direction
case object West extends Direction
case object Undefined extends Direction


abstract class Vehicle(_id: Int, struct: Structure, owner: Player) extends Renderable(new GridLocation(-1, -1)) {
  var weight : Double
  val cost : Int
  def id: Int = _id

  protected var _moving = BooleanProperty(false)
  def moving: BooleanProperty = _moving

  protected var _location: ObjectProperty[Structure] = ObjectProperty(struct)
  protected var _locationName = StringProperty(struct.name)
  _location.onChange { _locationName.set(_location.value.name) }

  protected var _nextLocation: ObjectProperty[Option[Structure]] = ObjectProperty(None)
  protected var _nextLocationName = StringProperty("-")
  _nextLocation.onChange {
    _nextLocation.value match {
      case Some(struct) => _nextLocationName.set(struct.name)
      case None => _nextLocationName.set("-")
    }
  }

  var arrived: Boolean = true
  var stabilized: Boolean = false

  def getDirs(origin: GridLocation, destination: GridLocation): ListBuffer[Direction] = {
    val dirs = new ListBuffer[Direction]()
    if (destination.row < origin.row || (destination.row == origin.row && origin.percentageHeight > 0)) dirs += North
    if (destination.col > origin.col) dirs += East
    if (destination.row > origin.row) dirs += South
    if (destination.col < origin.col || (destination.col == origin.col && origin.percentageWidth > 0)) dirs += West
    dirs
  }

  def location: Structure = _location.value
  def location_=(newStruct: Structure) = _location.set(newStruct)
  def locationName: StringProperty = _locationName
  def nextLocationName: StringProperty = _nextLocationName

  protected var _engine: ObjectProperty[Engine] = ObjectProperty(new Engine(owner))
  protected var _engineThrust: DoubleProperty = _engine.value.thrust
  def engineThrust: DoubleProperty = _engineThrust
  def engineUpgradeLevel: IntegerProperty = _engine.value.upgradeLevel
  def upgradeEngine(): Boolean = _engine.value.upgrade()

  protected var _speed = DoubleProperty(0)
  def speed: DoubleProperty = _speed

  def departure() = {
    arrived = false
    visible = true
    moving.set(true)
    stabilized = false
    speed <== engineThrust
  }

  def arrival() = {
    moving.set(false)
    speed <== DoubleProperty(0)
    _nextLocation.set(None)
  }

  def boarding(stops: ListBuffer[Structure]) = {
    _nextLocation.set(Some(stops(0)))
  }

  def landing() = {

  }

  def update(dt: Double, dirIndicator: Int): Unit

  def stabilize(pos: GridLocation, dt: Double, speed: Double): Boolean = {
    pos.percentageWidth = Math.max(pos.percentageWidth - dt * speed, 0)
    pos.percentageHeight = Math.max(pos.percentageHeight - dt * speed, 0)

    (pos.percentageHeight == 0 && pos.percentageWidth == 0)
  }

  // returns true iff the move lead to a change of case (ie percentage outbounds 0/100)
  def move(pos: GridLocation, dir: Direction, dt: Double, speed: Double): Boolean = {
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
}
