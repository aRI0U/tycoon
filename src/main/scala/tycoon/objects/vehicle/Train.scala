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



class Train(town : Town, nb_carriages : Int) extends Vehicle(town) {
  var location : Option[Structure] = Some(town)


  //Methods
  def rotation(angle : Int) = {
    //tile.getView.rotate = (angle)
  }

  def add_carriage () {
    for (i <- 1 to nb_carriages) carriages_list += new PassengerCarriage
    carriages_list += new GoodsCarriage
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
        case other => {println("tycoon > objects > vehicle > Train.scala > boarding: *boarding in train* case of location is not a town ")}
      }}
      case None => println("tycoon > objects > vehicle > Train.scala > boarding: no town")
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

  tile = Tile.locomotiveT

  var speed = 10
  var destination_x = 0
  var destination_y = 0
  val weight = 50
  val cost = 200

  var current_rail : Option[Rail] = None
  //var trail = road.rails


  var carriages_list = new ListBuffer[Carriage]()
  add_carriage()

  gridPos = location match {
    case Some(structure : Town) => {
      new GridLocation(structure.position.col +1,structure.position.row)
    }
    case Some(structure ) => structure.position
    case None => current_rail match {
      case Some(rail) => rail.position
      case None => new GridLocation(0,0) // throw exn
    }
  }
}
