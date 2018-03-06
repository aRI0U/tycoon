package tycoon.objects.carriage

import tycoon.ui.Renderable
import tycoon.objects.structure._

abstract class Carriage extends Renderable {
  val cost : Int
  val ticket_price : Int
  val weight : Int
  var passengers : Int

  def embark(town: Town) = { }

  def debark(town: Town) = { }
}
