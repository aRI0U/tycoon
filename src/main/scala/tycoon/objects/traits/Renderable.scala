package traits

import game.Position

trait Renderable {
  var pos : Position
  val sprite : String
  var visible : Boolean
}
