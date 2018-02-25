package tycoon.objects.structure

import tycoon.ui.Renderable

abstract class Structure(x:Integer, y:Integer) extends Renderable {
  var pos_x = x
  var pos_y = y
  var visible = true
}
