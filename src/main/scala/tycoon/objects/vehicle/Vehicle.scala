package tycoon.objects.vehicle

import tycoon.ui.Renderable
import tycoon.objects.railway.Road
import tycoon.objects.structure._
import tycoon.game.GridLocation


abstract class Vehicle(struct: Structure) extends Renderable(new GridLocation(-1, -1)) {
  val weight : Int
  val cost : Int
}
