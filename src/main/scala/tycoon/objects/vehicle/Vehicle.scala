package tycoon.objects.vehicle

import tycoon.ui.Renderable
import tycoon.objects.railway.Road
import tycoon.objects.structure._


abstract class Vehicle(town:Town) extends Renderable {
  val weight : Int
  val cost : Int
}
