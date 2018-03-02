package tycoon.objects.railway

import scala.util.Random
import tycoon.GridLocation
import tycoon.Game
import tycoon.ui.Sprite
import tycoon.ui.Tile

class BasicRail(pos: GridLocation) extends Rail(pos) {
  val cost = 10
  val max_speed = 50
  val max_weight = 1000
  val trail_head = false
  //temporary
  val tile = new Tile(Sprite.tile_straight_rail1)
  //create a numer 0 to 5 indicates witch tile choose with direction
  var tile_update = new Tile(Sprite.tile_straight_rail1)
  var origin = 0
  var orientation = 0
  //we should bind orientation with tile... or update while updating orientation..


  val tile_straight = new Tile(Sprite.tile_straight_rail1)
  val tile_turning = new Tile(Sprite.tile_turning_rail)
  gridLoc = pos
}
