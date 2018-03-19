package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import tycoon.game.Game
import tycoon.objects.carriage._
import tycoon.objects.railway._
import tycoon.objects.structure._
import scalafx.beans.property.{IntegerProperty, StringProperty}

abstract class Train(town : Town, nb_carriages : Int) extends Vehicle(town) {
  var carriages_list : ListBuffer[Carriage]
  var location : Option[Town] = Some(town)
  var current_rail : Option[Rail]

  // def rotation (angle : Int) {
  //   tile.getView.rotate = angle
  // }
  def rotation(angle : Int) = {
    tile.getView.rotate = (angle)
  }


  def add_carriage () {
    for (i <- 1 to nb_carriages) carriages_list += new BasicPassengerCarriage
    carriages_list += new BasicGoodsCarriage
  }

  def boarding () = {
    location match {
      case Some(t) => {
        for (carriage <- carriages_list) {
          carriage match {
            case PassengerCarriage() => carriage.embark(t)
            case _ => println("no passenger")
          }
        }
      }
      case None => println("no town")
    }
  }

  def landing () = {
    location match {
      case Some(t) => {
        for (carriage <- carriages_list) {
          carriage match {
            case PassengerCarriage() => carriage.debark(t)
            case _ => ;
          }
        }
      }
      case None => ;
    }
  }
}
