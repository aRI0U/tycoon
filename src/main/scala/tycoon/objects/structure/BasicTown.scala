package tycoon.objects.structure

import scala.util.Random
import tycoon.GridLocation
import tycoon.ui.Sprite
import tycoon.ui.Tile

class BasicTown(pos: GridLocation) extends Town(pos) {
  protected var population = 50 + r.nextInt(50)

  val tile = new Tile(Sprite.tile_house)
  gridLoc = pos
}
