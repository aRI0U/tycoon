package tycoon.objects.structure

import scala.util.Random
import tycoon.GridLocation
import tycoon.ui.Sprite
import tycoon.ui.Tile
import scalafx.beans.property.IntegerProperty

class BasicTown(pos: GridLocation) extends Town(pos) {
  population = 50 + r.nextInt(50)

  val tile = new Tile(Sprite.tile_house)
  gridLoc = pos
}
