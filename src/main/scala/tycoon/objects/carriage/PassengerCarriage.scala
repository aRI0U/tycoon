package tycoon.objects.carriage

import tycoon.objects.structure._
import tycoon.objects.railway.Rail
import tycoon.game.GridLocation
import tycoon.ui.Tile

case class PassengerCarriage() extends Carriage {
  var passengers : Int = 0

  override def embark(structure: Structure) : Unit = {
    structure match {
      case t:Town => {
        passengers = max_passengers.min(t.waiting_passengers)
        t.waiting_passengers -= passengers
        t.population -= passengers}
      case _ => ()
    }
  }

  override def debark(structure: Structure) : Unit = {
    structure match {
      case t:Town => {
        t.population = t.population + passengers
        passengers = 0}
      case f:Facility => {
        f.workers += passengers
        passengers = 0}
      case _ => ()
    }
  }

  tile = Tile.passenger_wagon

  val cost = 20
  val ticket_price = 3
  val weight = 100
  val max_passengers = 10
  var current_rail : Option[Rail] = None
  var currentLoc = new GridLocation(-1,-1)
}
