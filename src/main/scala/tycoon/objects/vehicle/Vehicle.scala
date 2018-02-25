package tycoon.objects.vehicle

import tycoon.ui.Renderable
import traits.Movable

abstract class Vehicle(x:Integer, y:Integer) extends Renderable with Movable {
  val weight : Integer
}
