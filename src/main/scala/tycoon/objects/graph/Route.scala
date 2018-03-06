package tycoon.objects.graph

import scala.collection.mutable.ListBuffer

import tycoon.objects.carriage._
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle._

class Route(itinerary : ListBuffer[Road], train : Train) {

  def departure (road: Road) = {
    train.location = None
    train.visible = true
  }

  def arrival (road: Road) = {
    train.location = road.end_town
    train.visible = false
  }

  train.current_rail = Some(itinerary(0).rails(0))

  // looking for the first rail of the trail
/*  for (rail <- road.rails) {
    if (rail.previous == rail) {
      current_rail = rail
    }
  }*/
  // private var counter = 0
  protected var intern_time : Double = 0

  def update_box (dt: Double, road:Road) = {
    train.current_rail match {
      case Some(rail) => {
        intern_time += dt
        if (intern_time > 1) {
          if (rail == rail.next) arrival(road)
          else {
            train.current_rail = Some(rail.next)
            train.gridLoc = rail.next.position
            intern_time = 0
          }
        }
      }
      case None => ;

        //need to orientate the locooo
       // if (current_rail.get_tile_type == 1) {
         // train.tile.getView.rotate = 90
    }
  }

  def update (dt : Double) {
    //update_box(dt)
    for (road <- itinerary) {
      train.location match {
        case Some(town) => departure(road)
        case None => update_box(dt, road)
      }
    }
  }
}
