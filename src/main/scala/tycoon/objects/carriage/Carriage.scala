package tycoon.objects.carriage

import tycoon.ui.Entity
import tycoon.objects.structure._
import tycoon.objects.railway._

abstract class Carriage extends Entity {
  val cost : Int
  val ticket_price : Int
  val weight : Int
  var passengers : Int
  var current_rail : Option[Rail]

  def rotation(angle : Int) = {
    tile.getView.rotate = (angle)
  }

  def embark(town: Town) = { }

  def debark(town: Town) = { }
}
