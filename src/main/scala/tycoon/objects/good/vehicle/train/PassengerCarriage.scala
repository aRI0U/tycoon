package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.game.{GridLocation, Player}
import tycoon.ui.Tile

case class PassengerCarriage(_owner: Player) extends Carriage(_owner) {
  var passengers : ListBuffer[(Structure, Int)] = new ListBuffer
  tile = Tile.passengerWagonR

  val ticketPrice = 3
  val maxPassengers = 10
  var remainingPlaces: Int = maxPassengers

  override def embark(departureStruct: Structure, stops: ListBuffer[Structure]) : Unit = {
    departureStruct match {
      case departureTown: Town => {
        for (stop <- stops) {
          if (remainingPlaces >= 1) {
            var newPassengers = 0
            stop match {
              case town: Town => {
                val i = departureTown.destinations.indexOf(town)
                newPassengers = remainingPlaces.min(departureTown.waiters(i))
                departureTown.waitersInt(i).set(departureTown.waiters(i) - newPassengers)
              }
              case facility: Facility => {
                newPassengers = remainingPlaces.min(departureTown.jobSeekers)
                departureTown.jobSeekers -= newPassengers
              }
              case _ => ()
            }
            departureTown.population -= newPassengers
            passengers += new Tuple2(stop, newPassengers)
            remainingPlaces -= newPassengers
            owner.earn(newPassengers * ticketPrice)
          }
        }
      }
      case _ => ()
    }
  }

  override def debark(s: Structure) : Unit = {
    for (p <- passengers) {
      if (p._1 == s) {
        remainingPlaces += p._2
        passengers -= p
        s match {
          case t: Town => t.population += p._2
          case f: Facility => f.workers += p._2
        }
      }
    }
    if (stops.isEmpty) println("PassengerCarriage > no more passengers")
  }

}
