package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.game.Player
import tycoon.ui.Tile
import tycoon.objects.structure._
import tycoon.objects.vehicle.LiquidContainer

class TankCar(_id: Int, initialTown: Structure, _owner: Player) extends GoodsCarriage(_id, initialTown, _owner) with LiquidContainer {
  override val tiles = Array(Tile.LiquidWagonT, Tile.LiquidWagonR, Tile.LiquidWagonB, Tile.LiquidWagonL)
}
