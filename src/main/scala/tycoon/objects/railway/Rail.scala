package tycoon.objects.railway

import scala.util.Random
import tycoon.GridLocation
import tycoon.Game
import tycoon.ui.Sprite
import tycoon.ui.Tile
import tycoon.ui.Renderable

abstract class Rail(pos: GridLocation, tile_type: Int) extends Renderable {
  val cost : Int
  val max_speed : Int
  val max_weight : Int
  var road_head : Boolean

  var road = new Road(pos)
  road.length+=1
  road.rails += this

  var next = this
  var previous = this
  var origin = 0
  var orientation = 0

  def position : GridLocation = pos
  def get_tile_type : Int = tile_type
}
