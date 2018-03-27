package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import tycoon.game.Game
import tycoon.objects.carriage._
import tycoon.objects.railway._
import tycoon.objects.structure._
import scalafx.beans.property.{IntegerProperty, StringProperty}
import tycoon.ui.Tile
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.DraggableTiledPane


class Train(town: Town, nb_carriages: Int) extends Vehicle(town) {
  var location: Option[Structure] = Some(town)

  def addCarriages() { // ??
    for (i <- 1 to nb_carriages) carriageList += new PassengerCarriage
    carriageList += new GoodsCarriage
  }

  def boarding(itinerary: ListBuffer[Road]) = {
    location match {
      case Some(struct) => {
        struct match {
          case town: Town => carriageList.foreach(_.embark(town, itinerary))
          case _ => println("tycoon > objects > vehicle > Train.scala > boarding: *boarding in train* case of location is not a town")
        }
      }
      case None => println("tycoon > objects > vehicle > Train.scala > boarding: no town")
    }
  }

  def landing() = {
    location match {
      case Some(struct) => carriageList.foreach(_.debark(struct))
      case None => ()
    }
  }

  tile = Tile.locomotiveT

  var speed = 2.0
  val weight = 50
  val cost = 200

  var current_rail : Option[Rail] = None
  //var trail = road.rails


  var carriageList = new ListBuffer[Carriage]()
  addCarriages()

  gridPos = location match {
    case Some(town: Town) => town.gridPos.right
    case Some(struct) => struct.gridPos
    case None => current_rail match {
      case Some(rail) => rail.gridPos
      case None => new GridLocation(0, 0) // throw exn
    }
  }
}
