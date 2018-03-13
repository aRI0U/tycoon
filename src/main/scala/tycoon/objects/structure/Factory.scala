package tycoon.objects.structure

import scala.util.Random
import tycoon.GridLocation
import tycoon.Game
import tycoon.ui.Tile

case class Factory(pos: GridLocation, id: Int) extends Structure(pos, id) {
  val tile = new Tile(Tile.factory_tile)
  //val price = game.mine_price //To choose
  gridLoc = pos
}
