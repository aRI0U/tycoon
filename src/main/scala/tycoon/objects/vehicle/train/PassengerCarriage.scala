package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.game.{GridLocation, Player}
import tycoon.ui.Tile
import tycoon.game.Settings

case class PassengerCarriage(_id: Int, initialTown: Structure, _owner: Player) extends Carriage(_id, initialTown, _owner) {
  var passengers : ListBuffer[(Structure, Int)] = new ListBuffer
  tile = Tile.PassengerWagonR
  val tiles = Array(Tile.PassengerWagonT, Tile.PassengerWagonR, Tile.PassengerWagonB, Tile.PassengerWagonL)

  var remainingPlaces: Int = Settings.TrainMaxPassengers

  def embark(departureStruct: Structure, stops: ListBuffer[Structure]) : Unit = {
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
            owner.earn(newPassengers * Settings.TrainTicketPrice)
          }
        }
      }
      case _ => ()
    }
  }

  def debark(s: Structure) : Unit = {
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
