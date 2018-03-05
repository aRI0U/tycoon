package tycoon.objects.railway

import scala.util.Random
import tycoon.GridLocation
import tycoon.Game
import tycoon.ui.Sprite
import tycoon.ui.Tile

case class BasicRail(pos: GridLocation, tile_type : Int) extends Rail(pos) {
  val cost = 10
  val max_speed = 50
  val max_weight = 1000
  var road_head = true

  var road = new Road(pos)
  road.length+=1
  road.rails += this

  var next = this
  var previous = this
  var origin = 0
  var orientation = 0

  def position : GridLocation = pos
  def get_tile_type : Int = tile_type

  def gives_tile (i : Int) : Tile = {
    if (i==0) return (new Tile(Sprite.tile_straight_rail1))
    else return ( new Tile(Sprite.tile_turning_rail) )
  }

  val tile = gives_tile(tile_type)
  gridLoc = pos
}
