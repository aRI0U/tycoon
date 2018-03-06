package tycoon.objects.carriage

import tycoon.ui.Renderable
import tycoon.objects.structure._

abstract class Carriage extends Renderable {
  val cost : Int
  val ticket_price : Int
  val weight : Int
  val max_passengers : Int
  var passengers : Int

  def embark(town: Town) = {
    passengers = max_passengers.min(town.waiting_passengers)
    town.population -= passengers
    town.waiting_passengers -= passengers
  }

  def debark(town: Town) = {
    town.population += passengers
    passengers = 0
  }
}
