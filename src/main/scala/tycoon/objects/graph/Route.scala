package tycoon.objects.graph

import scala.collection.mutable.ListBuffer

import tycoon.objects.carriage._
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle._
import tycoon.Game

class Route(itinerary : ListBuffer[Road], train : Train,game : Game) {

  var current_road : Option[Road]= None
  println (itinerary)

  def departure () = {
    train.location = None
    train.visible = true
    current_road = Some(itinerary(0))
    itinerary.remove(0)
    //train.current_rail = current_road.rails
  }

  def arrival (road: Road) = {
    train.location = road.end_town
    train.visible = false
    train.current_rail = None
  }

  println(itinerary(0).rails(0))
  train.current_rail = Some(itinerary(0).rails(0))
  train.gridLoc = train.current_rail.get.position
  //in order  to update the graphic
  game.tiledPane.layoutEntities

  // looking for the first rail of the trail
/*  for (rail <- road.rails) {
    if (rail.previous == rail) {
      current_rail = rail
    }
  }*/
  private var counter = 0
  protected var intern_time : Double = 0

  def update_box (dt: Double, road:Road) = {
    train.current_rail match {
      case Some(rail) => {
        intern_time += dt
        if (intern_time > 1) {
          if (rail == rail.next) arrival(road)
          else {
            println(counter)
            counter+=1
            train.current_rail = Some(rail.next)
            train.gridLoc = rail.next.position
            intern_time -=1
            //in order  to update the graphic
            game.tiledPane.layoutEntities
          }
        }
      }
      case None => {
      }

        //need to orientate the locooo
       // if (current_rail.get_tile_type == 1) {
         // train.tile.getView.rotate = 90
    }
  }


  def update (dt : Double) {
    //update_box(dt)
    //for (road <- itinerary)

      train.location match {
        case Some(town) => departure()
        case None => update_box(dt, current_road.get)
      }
    //}
  }
}
