package tycoon.objects.railway

import scala.util.Random
import tycoon.GridLocation
import tycoon.Game
import tycoon.ui.Sprite
import tycoon.ui.Tile

case class BasicRail(pos: GridLocation, val tile_type : Int) extends Rail(pos, tile_type) {
  val cost = 10
  val max_speed = 50
  val max_weight = 1000
  var road_head = true

  def gives_tile (i : Int) : Tile = {
    if (i==0) return (new Tile(Sprite.tile_straight_rail1))
    else return ( new Tile(Sprite.tile_turning_rail) )
  }

  val tile = gives_tile(tile_type)
  gridLoc = pos
}
