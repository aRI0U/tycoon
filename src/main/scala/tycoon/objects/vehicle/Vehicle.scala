package tycoon.objects.vehicle

import tycoon.ui.Renderable
import tycoon.objects.railway.Road
import tycoon.objects.structure._
import tycoon.game.GridLocation


abstract class Vehicle(town:Town) extends Renderable(new GridLocation(-1, -1)) {
  val weight : Int
  val cost : Int
}
