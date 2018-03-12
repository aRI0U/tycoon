package tycoon.objects.structure

import scala.util.Random
import tycoon.GridLocation
import tycoon.Game
import tycoon.ui.Tile

case class Mine(pos: GridLocation, id: Int) extends Structure(pos, id) {
  protected val r = scala.util.Random

  protected var ore_amount = 50 + r.nextInt(50)
  val tile = new Tile(Tile.mine)
  //val price = game.mine_price //To choose
  gridLoc = pos
}
