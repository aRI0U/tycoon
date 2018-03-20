package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import tycoon.game.Game
import tycoon.objects.carriage._
import tycoon.objects.railway._
import tycoon.objects.structure._
import scalafx.beans.property.{IntegerProperty, StringProperty}

abstract class Train(town : Town, nb_carriages : Int) extends Vehicle(town) {
  var carriages_list : ListBuffer[Carriage]
  var location : Option[Structure] = Some(town)
  var current_rail : Option[Rail]


  //Methods
  def rotation(angle : Int) = {
    tile.getView.rotate = (angle)
  }

  def add_carriage () {
    for (i <- 1 to nb_carriages) carriages_list += new BasicPassengerCarriage
    carriages_list += new BasicGoodsCarriage
  }

  def boarding () = {
    location match {
      case (Some(struc)) => {
        struc match {
          case (town : Town) => {
            for (carriage <- carriages_list) {
              carriage match {
                case p:PassengerCarriage =>
                p.embark(town)
                case _ => ()
              }
            }
          }
        case other => {println("*boarding in train* case of location is not a town ")}
      }}
      case None => println("no town")
    }
  }

  def landing () = {
    location match {
      case (Some(s)) => {
        for (carriage <- carriages_list) carriage.debark(s)
      }
      case None => ()
    }
  }
}
