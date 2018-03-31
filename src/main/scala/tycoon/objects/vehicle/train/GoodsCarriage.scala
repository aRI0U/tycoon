package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._
import tycoon.objects.good._
import tycoon.ui.Renderable
import tycoon.ui.Tile
import tycoon.game.{GridLocation, Player}
import tycoon.objects.railway._


case class GoodsCarriage(_owner: Player) extends Carriage(_owner) {
  tile = Tile.goodsWagonR

  override def debark(structure: Structure) = {
    structure match {
      case t: Town => ()
      case f: Facility => {
        for (i <- 0 to f.products.length-1) {
          // hardcoded only for a test
          if (f.products(i).label == "Coal") {
            f.stocksInt(i).set(50)
          }
        }
      }
    }
  }

}
