package tycoon.objects.railway

import scala.util.Random
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile

case class BasicRail(pos: GridLocation, val tile_type : Int) extends Rail(pos, tile_type) {
  tile = gives_tile(tile_type)

  val cost = 10
  val max_speed = 50
  val max_weight = 1000
  var road_head = true

  def gives_tile (i : Int) : Tile = {
    if (i==0) Tile.straight_rail1
    else Tile.turning_rail
  }

  def get_tile_type : Int = {
    println ("tycoon > objects > railway > BasicRail.scala > get_tile_type: get type:" + tile_type)
    tile_type
  }

  var nb_rotation = 2
}
