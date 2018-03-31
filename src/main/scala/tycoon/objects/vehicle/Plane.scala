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


class Plane(airport: Airport, val owner: Player) extends Vehicle(airport) {
  var location: Structure = airport
  var onTheRoad = BooleanProperty(false)
  tile = Tile.plane
  var speed = DoubleProperty(200.0)
  val weight = 50
  val cost = 500
  // var from = StringProperty(town.name)

  // addCarriages()
  //
  gridPos = location match {
    case town: Town => town.gridPos.right
    case struct: Structure => struct.gridPos
  }
  //
  // def addCarriages() { // ??
  //   for (i <- 1 to nbCarriages.value) carriageList += new PassengerCarriage(owner)
  //   carriageList += new GoodsCarriage(owner)
  // }
  //
  // def boarding(stops: ListBuffer[Structure]) = {
  //   onTheRoad.set(true)
  //   carriageList foreach (_.embark(location, stops))
  // }
  //
  // def landing() = {
  //   onTheRoad.set(false)
  //   carriageList.foreach(_.debark(location))
  // }
}