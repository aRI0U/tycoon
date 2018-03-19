package tycoon.objects.carriage

import tycoon.objects.structure._

abstract case class PassengerCarriage() extends Carriage {
  val ticket_price : Int
  val max_passengers : Int
  var passengers : Int

  override def embark(structure: Structure) : Unit = {
    structure match {
      case s:Town => {passengers = max_passengers.min(s.waiting_passengers)
        s.waiting_passengers -= passengers
        s.population -= passengers}
      case _ => ()
    }
  }

  override def debark(town: Town) : Unit = {
    town.population += passengers
    passengers = 0
  }
}
