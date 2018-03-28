package tycoon.objects.structure

import scala.util.Random
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile

case class Farm(pos: GridLocation, id: Int) extends Facility(pos, id) {
  tile = Tile.farm(0)
  var tileType = 1
  //val price = game.farmPrice //To choose
  var production_time = 100

  override def update(dt: Double) = {
    intern_time += dt
    if(intern_time > production_time) {
      tile = Tile.farm(tileType)
      tileType += 1
      intern_time -= production_time
    }
  }

}
