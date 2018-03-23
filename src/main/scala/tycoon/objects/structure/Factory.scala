package tycoon.objects.structure

import scala.util.Random
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile

case class Factory(pos: GridLocation, id: Int) extends Structure(pos, id) {
  tile = Tile.factory
}
