package tycoon.objects.carriage

import tycoon.ui.Renderable
import tycoon.objects.structure._

abstract class Carriage extends Renderable {
  val cost : Int
  val weight : Int
  def embark(town: Town) : Unit = {}
  def debark(town: Town) : Unit = {}
}
