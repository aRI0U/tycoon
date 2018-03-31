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

  def location: Structure = _location.value
  def location_=(newStruct: Structure) = _location.set(newStruct)
  def locationName: StringProperty = _locationName




  tile = Tile.locomotiveT
  var speed = DoubleProperty(200.0) // in tile size percentage per second (ie in tile * 100 / s), here 2tiles/sec
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

  def addCarriages() { // ??
    for (i <- 1 to nbCarriages.value) carriageList += new PassengerCarriage(owner)
    carriageList += new GoodsCarriage(owner)
  }

  def boarding(stops: ListBuffer[Structure]) = {
    moving.set(true)
    carriageList foreach (_.embark(location, stops))
  }

  def landing() = {
    moving.set(false)
    carriageList.foreach(_.debark(location))
  }
}
