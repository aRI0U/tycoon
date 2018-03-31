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
    speed.set(engineThrust.value) // modulo weight of carriages here..
    _nextLocation.set(Some(stops.last)) // NE FONCTIONNE PAS YA UN PB AVEC LES STOPS
    carriageList foreach (_.embark(location, stops))
  }

  def landing() = {
    moving.set(false)
    speed.set(0)
    _nextLocation.set(None)
    carriageList.foreach(_.debark(location))
  }
}
