package tycoon.objects.structure

import scala.util.Random
import tycoon.game.{GridLocation,Player}
import tycoon.game.Game
import tycoon.ui.Tile

case class Dock(pos: GridLocation, id: Int, override val owner: Player) extends Structure(pos, id, owner) {
  tile = Tile.Dock
  var dependanceTown : Option[Town]= None
  setName("Dock " + id.toString)
  def update(dt: Double) = {

  }
}
