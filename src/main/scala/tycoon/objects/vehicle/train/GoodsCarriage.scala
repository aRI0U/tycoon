package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.game.{GridLocation, Player}
import tycoon.objects.good._
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle.Container
import tycoon.ui.Renderable
import tycoon.ui.Tile

import scalafx.beans.property.IntegerProperty


case class GoodsCarriage(_id: Int, initialTown: Structure, override val owner: Player) extends Carriage(_id, initialTown, owner) with Container {
  tile = Tile.GoodsWagonR
  val tiles = Array(Tile.GoodsWagonT, Tile.GoodsWagonR, Tile.GoodsWagonB, Tile.GoodsWagonL)
  val maxSpace : Double = 100
  var remainingSpace : Double = maxSpace
  val merchandises = new ListBuffer[Merchandise]

  val mManager = new MerchandisesManager
}
