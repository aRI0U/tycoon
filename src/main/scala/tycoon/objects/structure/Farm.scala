package tycoon.objects.structure

import scala.util.Random
import tycoon.GridLocation
import tycoon.Game
import tycoon.ui.Tile

case class Farme(pos: GridLocation, id: Int) extends Structure(pos, id) {
  val tile = new Tile(Tile.farm_tile)
  //val price = game.mine_price //To choose
  gridLoc = pos
}
