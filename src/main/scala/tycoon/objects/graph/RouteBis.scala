package tycoon.objects.graph

import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.ui.Tile
import tycoon.objects.vehicle.train._
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle._
import tycoon.game.Game
import tycoon.game.GridLocation

class RouteBis(itinerary: ListBuffer[Road], train: Train) {
  private var onTheRoad = true
  private var dirIndicator = 1


  var currentRoad: Option[Road] = None
  val stops: ListBuffer[Structure] = determineStops(itinerary)


  def determineStops(itinerary: ListBuffer[Road]) : ListBuffer[Structure] = {
    var stops = new ListBuffer[Structure]
    for (road <- itinerary) {
      road.startStructure match {
        case Some(s) => {
          road.endStructure match {
            case Some(e) => {
              stops += s
              stops += e
            }
            case None => println("tycoon > objects > graph > Route : unfinished road (not supposed to happen)")
          }
        }
        case None => println("tycoon > objects > graph > Route : unfinished road (not supposed to happen)")
      }
    }
    stops.distinct
  }

}
