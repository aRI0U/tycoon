package tycoon.objects.carriage

import tycoon.ui.Renderable
import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.game.GridLocation

abstract class Carriage extends Renderable(new GridLocation(-1, -1)) {
  val cost : Int
  val weight : Int
  var current_rail : Option[Rail]
  var currentLoc : GridLocation

  def rotation(angle : Int) = {
    //tile.getView.rotate = (angle)
  }

  def embark(structure: Structure) = { }
  def debark(structure: Structure) = { }

}
