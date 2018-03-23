package tycoon.objects.railway

import scala.util.Random
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile
import tycoon.ui.Renderable
import scalafx.beans.property.{IntegerProperty, StringProperty}

case class Rail(pos: GridLocation) extends Renderable(pos) {//, tile_type: Int) extends Renderable(pos) {

  var road = new Road
  road.length = 1
  road.rails += this

  var next : Rail = this
  var previous : Rail = this
  var origin = 0
  var orientation = 0

  def direction(i : Int) : Rail = {
    if (i == 0) return next
    else return previous
  }

  def position : GridLocation = pos
  def get_rotation : Int = nb_rotation


  tile = Tile.straight_rail1 // sgives_tile(tile_type)

  val cost = 10
  val max_speed = 50
  val max_weight = 1000
  var road_head = true

  def gives_tile (i : Int) : Tile = {
    if (i==0) Tile.straight_rail1
    else Tile.turning_rail
  }


  var nb_rotation = 2
}
