package tycoon.objects.structure

import scala.util.Random
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile

case class Dock(pos: GridLocation, id: Int) extends Structure(pos, id) {
  tile = Tile.dock
  var dependanceTown : Option[Town]= None
  def update(dt: Double) = {
  }
}
