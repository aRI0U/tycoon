package tycoon.objects.vehicle

import tycoon.ui.Renderable
import tycoon.objects.railway.Road


abstract class Vehicle(road : Road) extends Renderable {
  val weight : Int
}
