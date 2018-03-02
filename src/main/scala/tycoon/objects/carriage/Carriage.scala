package tycoon.objects.carriage

import tycoon.ui.Renderable

abstract class Carriage extends Renderable {
  val cost : Int
  val weight : Int
}
