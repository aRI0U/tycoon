package tycoon.objects.structure

import scala.util.Random
import tycoon.game.{GridLocation,Player}
import tycoon.game.Game
import tycoon.ui.Tile

case class Field(pos: GridLocation, id: Int, owner: Player) extends Structure(pos, id, owner) {
  tile = Tile.Field(0)
  setName("Field " + id.toString)
  var tileType = 0
  var dependanceFarm : Option[Farm] = None
  def update(dt: Double) = {

  }
}
