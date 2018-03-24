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
            val i = t.destinations.indexOf(s)
            println(stops)
            println(t.destinations)
            println(s)
            println(i)
            println(t.waitersInt.length)
            val new_passengers = remaining_places.min(t.waiters(i))
            t.population -= new_passengers
            t.waitersInt(i).set(t.waiters(i) - new_passengers)
            passengers += new Tuple2(s, new_passengers)
            remaining_places -= new_passengers
          }
        }
      }
      case _ => ()
    }
  }

  override def debark(structure: Structure) : Unit = {
    structure match {
      case t:Town => {
        for (p <- passengers) {
          if (p._1 == t) {
            t.population += p._2
            remaining_places += p._2
            passengers -= p
          }
        }
      }
  //     case f:Facility => {
  //       f.workers += passengers
  //       passengers = 0}
      case _ => ()
    }
    stops -= structure
    if (stops.isEmpty) println("finished trip")
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
