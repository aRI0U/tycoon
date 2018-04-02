package tycoon.objects.structure

import scala.util.Random
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile

case class Field(pos: GridLocation, id: Int) extends Structure(pos, id) {
  tile = Tile.field1
  var tileType = 0
  var dependanceFarm : Option[Farm] = None
  def update(dt: Double) = {
  }
}
