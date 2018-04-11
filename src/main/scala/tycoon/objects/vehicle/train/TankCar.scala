package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.game.Player
import tycoon.objects.structure._
import tycoon.objects.vehicle.LiquidContainer

class TankCar(_id: Int, initialTown: Structure, _owner: Player) extends GoodsCarriage(_id, initialTown, _owner) with LiquidContainer {


}
