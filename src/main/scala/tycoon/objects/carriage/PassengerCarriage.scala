package tycoon.objects.carriage

import tycoon.objects.structure._

abstract case class PassengerCarriage() extends Carriage {
  val ticket_price : Int
  val max_passengers : Int
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
}
