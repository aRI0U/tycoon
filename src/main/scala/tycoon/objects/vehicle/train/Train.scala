package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.game.Game
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle.Vehicle
import scalafx.beans.property.{IntegerProperty, StringProperty}
import tycoon.ui.Tile
import tycoon.game.{Game, GridLocation, Player}
import tycoon.ui.DraggableTiledPane


class Train(town: Town, nbCarriages: Int, val owner: Player) extends Vehicle(town) {
  var location: Option[Structure] = Some(town)
  tile = Tile.locomotiveT
  var speed = 2.0
  val weight = 50
  val cost = 200
  var currentRail : Option[Rail] = None
  var carriageList = new ListBuffer[Carriage]()
  addCarriages()

  gridPos = location match {
    case Some(town: Town) => town.gridPos.right
    case Some(struct) => struct.gridPos
    case None => currentRail match {
      case Some(rail) => rail.gridPos
      case None => new GridLocation(-1, -1)
    }
  }

  def addCarriages() { // ??
    for (i <- 1 to nbCarriages) carriageList += new PassengerCarriage(owner)
    carriageList += new GoodsCarriage(owner)
  }

  def boarding(stops: ListBuffer[Structure]) = {
    location match {
      case Some(struct) => carriageList foreach (_.embark(struct, stops))
      case None => ()
    }
  }

  def landing() = {
    location match {
      case Some(struct) => carriageList.foreach(_.debark(struct))
      case None => ()
    }
  }
}
