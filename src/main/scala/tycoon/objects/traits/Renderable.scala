package traits

import game.Position

trait Renderable {
  var pos_x : Integer
  var pos_y : Integer
  val sprite : String
  var visible : Boolean
}
