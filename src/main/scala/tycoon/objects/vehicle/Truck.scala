package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import tycoon.game.Game
import tycoon.objects.good._
import tycoon.objects.railway._
import tycoon.objects.structure._
import scalafx.beans.property._
import tycoon.ui.Tile
import tycoon.game.{Game, GridLocation, Player}
import tycoon.ui.DraggableTiledPane
import tycoon.game.Settings


class Truck(_id: Int, initialStruct: Structure, val owner: Player) extends Vehicle(_id, initialStruct, owner) with Container {
  val maxSpace : Double = 100
  var weight = 50
  var consumption = 1
  var remainingSpace : Double = maxSpace
  val merchandises = new ListBuffer[Merchandise]
  val mManager = new MerchandisesManager

  // dynamic values
  accDistance = 2.0
  decDistance = 1.0
  initialSpeed = 0.2

  def accFunction (d: Double) : Double = Math.sqrt(d)
  def decFunction (d: Double) : Double = Math.sqrt(d)

  var onTheRoad = BooleanProperty(false)
  tile = Tile.Truck
  gridPos = location.gridPos.clone()

  override def boarding(stops: ListBuffer[Structure]) = {
    super.boarding(stops)
    embark(location, stops)
  }

  override def landing() = {
    super.landing
    debark(location)
  }
}
