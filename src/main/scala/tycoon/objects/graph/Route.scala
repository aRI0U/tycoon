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

  var current_rail = itinerary(0).rails(0)

  // looking for the first rail of the trail
/*  for (rail <- road.rails) {
    if (rail.previous == rail) {
      current_rail = rail
    }
  }*/
  // private var counter = 0
  protected var intern_time : Double = 0

  def update_box (dt: Double, road:Road) = {
    intern_time += dt
    if (intern_time > 1) {
      if (current_rail == current_rail.next) arrival(road)
      else {
        current_rail = current_rail.next
        train.gridLoc = current_rail.position
        intern_time = 0

        //need to orientate the locooo
       // if (current_rail.get_tile_type == 1) {
         // train.tile.getView.rotate = 90
      }
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
