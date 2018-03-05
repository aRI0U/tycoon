package tycoon.objects.structure

import scala.util.Random
import tycoon.GridLocation
import tycoon.Game
import tycoon.ui.Sprite
import tycoon.ui.Tile

class Mine(pos: GridLocation) extends Structure(pos) {
  protected val r = scala.util.Random

  protected var ore_amount = 50 + r.nextInt(50)
  val tile = new Tile(Sprite.tile_mine)
  //val price = game.mine_price //To choose
  gridLoc = pos
}
