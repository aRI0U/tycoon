package structure

import traits.Renderable

abstract class Structure(x:Integer, y:Integer) extends Renderable {
  var pos_x = x
  var pos_y = y
  var visible = true
}
