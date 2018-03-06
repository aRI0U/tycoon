package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import tycoon.objects.carriage._
import tycoon.objects.railway._
import tycoon.objects.structure._

abstract case class Train(town : Town) extends Vehicle(town) {
  var carriagesList : ListBuffer[Carriage]
  var location : Option[Town] = Some(town)
  var visible : Boolean
  var current_rail : Option[Rail]

  def boarding () = {
    location match {
      case Some(town) => {
        for (carriage <- carriagesList) {
          carriage match {
            case PassengerCarriage() => carriage.embark(town)
            case _ => ;
          }
        }
      }
      case None => ;
    }
  }

  def landing () = {
    location match {
      case Some(town) => {
        for (carriage <- carriagesList) {
          carriage match {
            case PassengerCarriage() => carriage.debark(town)
            case _ => ;
          }
        }
      }
      case None => ;
    }
  }


/*  var current_rail = road.rails(0)

  // looking for the first rail of the trail
  for (rail <- road.rails) {
    if (rail.previous == rail) {
      current_rail = rail
    }
  }
  // private var counter = 0
  private var intern_time : Double = 0

  def update_box (dt: Double) = {
    intern_time += dt
    if (intern_time > 1) {
      current_rail = current_rail.next
      gridLoc = current_rail.position
      intern_time = 0

      //need to orientate the locooo
      if (current_rail.get_tile_type == 1) {
        tile.getView.rotate = 90
      }
    }
  }

  def update (dt : Double) {
    location match {
      case Some(town) => departure()
      case None => update_box(dt)
    }
    arrival()
  }
 */
}
