package tycoon.objects.structure

import scala.util.Random
import tycoon.GridLocation
import tycoon.Game
import tycoon.ui.Tile

case class Farm(pos: GridLocation, id: Int) extends Structure(pos, id) {
  tile = new Tile(Tile.farm_tile)
  //val price = game.mine_price //To choose
  setPos(pos)
}
