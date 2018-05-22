package tycoon.objects.structure

import tycoon.game.Game
import tycoon.game.GridLocation
import tycoon.ui.Tile
import tycoon.ui.Renderable

class Flag (pos : GridLocation, ai : Int ) extends Renderable(pos) {
  tile = Tile.BlueFlag
  if (ai == 1) tile = Tile.RedFlag
}
