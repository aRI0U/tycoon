package tycoon.objects.railway

import tycoon.ui.Renderable

abstract class Rail(x: Int, y: Int) extends Renderable {
  val cost : Int
  val max_speed : Int
  val max_weight : Int
}
