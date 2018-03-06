package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import tycoon.objects.carriage._
import tycoon.objects.railway._
import tycoon.objects.structure._

abstract class Train(town : Town, nb_carriages : Int) extends Vehicle(town) {
  var carriages_list : ListBuffer[Carriage]
  var location : Option[Town] = Some(town)
  var visible : Boolean
  var current_rail : Option[Rail]

  def add_carriage () {
    for (i <- 1 to nb_carriages) carriages_list += new BasicPassengerCarriage
  }

  def boarding () = {
    location match {
      case Some(t) => {
        println("ah")
        for (carriage <- carriages_list) {
          println("oh")
          carriage match {
            case PassengerCarriage() => carriage.embark(t)
            case _ => println("no passenger")
          }
        }
      }
      case None => println("notown")
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
