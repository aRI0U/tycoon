package carriage

import traits.Renderable
import traits.Movable

abstract class Carriage extends Renderable with Movable {
  val cost : Integer
  val weight : Integer
}
