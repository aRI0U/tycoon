package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import tycoon.game.Player
import tycoon.objects.structure._

trait PassengerTransporter {

  var passengers : ListBuffer[(Structure, Int)] = new ListBuffer

  var remainingPlaces : Int = 0
  val price : Int
  val salesman : Player

  def embarkP(departureStruct: Structure, stops: ListBuffer[Structure]) = {
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
                departureTown.totalWaiters -= newPassengers
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
            salesman.earn(newPassengers * price)
          }
        }
      }
      case _ => ()
    }
  }

  def debarkP(s: Structure) = {
    for (p <- passengers) {
      if (p._1 == s) {
        remainingPlaces += p._2
        passengers -= p
        s match {
          case t: Town => {
            t.population += p._2
            t.alive = true
          }
          case f: Facility => f.workers += p._2
        }
      }
    }
  }
}
