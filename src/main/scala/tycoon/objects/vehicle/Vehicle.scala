package tycoon.objects.vehicle

import tycoon.ui.Renderable


abstract class Vehicle(x: Int, y: Int) extends Renderable {
  val weight : Int
}
