package tycoon.objects.carriage

import tycoon.ui.Renderable
import traits.Movable

abstract class Carriage extends Renderable with Movable {
  val cost : Integer
  val weight : Integer
}
