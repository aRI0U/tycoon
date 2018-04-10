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


class Truck(_id: Int, initialStruct: Structure, val owner: Player) extends Vehicle(_id, initialStruct, owner) with Container {

  val maxSpace : Double = 100
  var remainingSpace : Double = maxSpace
  val merchandises = new ListBuffer[Merchandise]

  val mManager = new MerchandisesManager

  var onTheRoad = BooleanProperty(false)
  tile = Tile.truck
  speed.set(200.0)
  var weight = 50
  val cost = 500
  gridPos = location.gridPos.clone()



  def update(dt: Double, dirIndicator: Int) = {

  }
}
