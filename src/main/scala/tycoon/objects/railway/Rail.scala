package tycoon.objects.railway

import tycoon.ui.Renderable

abstract class Rail(x:Integer, y:Integer) extends Renderable {
  val cost : Integer
  val max_speed : Integer
  val max_weight : Integer
  val sprite = "bite"
  var pos_x = x
  var pos_y = y
  var visible = true
}
