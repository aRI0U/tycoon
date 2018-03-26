package tycoon.objects.carriage

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.game.GridLocation
import tycoon.ui.Tile

case class PassengerCarriage() extends Carriage {
  var passengers : ListBuffer[(Structure, Int)] = new ListBuffer

  override def embark(structure: Structure, itinerary: ListBuffer[Road]) : Unit = {
    if (stops.isEmpty) {
      stops = determineStops(itinerary)
      stops -= structure}
    structure match {
      case t:Town => {
        for (s <- stops) {
          if (remaining_places > 0) {
            var new_passengers = 0
            s match {
              case town: Town => {
                val i = t.destinations.indexOf(s)
                println("stops:" + stops)
                println("destinations:" + t.destinations)
                println(s)
                println(i)
                println(t.waitersInt.length)
                new_passengers = remaining_places.min(t.waiters(i))
                t.waitersInt(i).set(t.waiters(i) - new_passengers)
              }
              case f: Facility => {
                new_passengers = remaining_places.min(t.jobSeekers)
                t.jobSeekers -= new_passengers
              }
            }
            t.population -= new_passengers
            passengers += new Tuple2(s, new_passengers)
            remaining_places -= new_passengers
          }
        }
      }
      case _ => ()
    }
  }

  override def debark(s: Structure) : Unit = {
    for (p <- passengers) {
      if (p._1 == s) {
        remaining_places += p._2
        passengers -= p
        s match {
          case t: Town => t.population += p._2
          case f: Facility => f.workers += p._2
        }
      }
    }
    stops -= s
    if (stops.isEmpty) println("PassengerCarriage > no more passengers")
  }

  tile = Tile.passengerWagonR

  val cost = 20
  val ticket_price = 3
  val weight = 100
  val max_passengers = 10
  var remaining_places : Int = max_passengers
  var current_rail : Option[Rail] = None
  var currentLoc = new GridLocation(-1,-1)
}
