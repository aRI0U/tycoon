package tycoon.objects.railway

import scala.util.Random
import tycoon.GridLocation
import tycoon.Game
import tycoon.ui.Sprite
import tycoon.ui.Tile
import tycoon.ui.Renderable

abstract class Rail(pos: GridLocation) extends Renderable {
  val cost : Int
  val max_speed : Int
  val max_weight : Int
  val trail_head : Boolean
}
