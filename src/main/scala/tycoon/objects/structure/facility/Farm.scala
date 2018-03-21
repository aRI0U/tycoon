package tycoon.objects.structure

import scala.util.Random
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile

case class Farm(pos: GridLocation, id: Int) extends Facility(pos, id) {
  tile = new Tile(Tile.farm_tile)
  //val price = game.mine_price //To choose
}
