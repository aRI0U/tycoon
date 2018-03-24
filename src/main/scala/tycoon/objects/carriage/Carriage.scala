package tycoon.objects.carriage

import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.game.GridLocation

abstract class Carriage extends Renderable(new GridLocation(-1, -1)) {
  val cost : Int
  val weight : Int
  var current_rail : Option[Rail]
  var currentLoc : GridLocation
  var stops = new ListBuffer[Structure]

  def rotation(angle : Int) = {
    //tile.getView.rotate = (angle)
  }

  def embark(structure: Structure, itinerary: ListBuffer[Road]) = { }
  def debark(structure: Structure) = { }

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
