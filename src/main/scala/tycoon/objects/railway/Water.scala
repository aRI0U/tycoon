package tycoon.objects.railway

import scala.util.Random
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile
import tycoon.ui.Renderable
import scalafx.beans.property.{IntegerProperty, StringProperty}

case class Water(pos: GridLocation) extends RoadItem(pos) {

  tile = Tile.water(0) // sgives_tile(tile_type)

  val cost = 10
  val max_speed = 50
  val max_weight = 1000
}
