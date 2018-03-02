package tycoon.objects.railway

import scala.util.Random
import tycoon.GridLocation
import tycoon.Game
import tycoon.ui.Sprite
import tycoon.ui.Tile
import scala.collection.mutable.{HashMap, ListBuffer}

class Trail(rails : ListBuffer[Rail]) {

  //temporary
  val tile = new Tile(Sprite.tile_straight_rail1)

  val tile_straight = new Tile(Sprite.tile_straight_rail1)
  val tile_turning = new Tile(Sprite.tile_turning_rail)
}
