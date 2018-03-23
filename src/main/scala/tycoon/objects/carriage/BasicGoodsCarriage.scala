package tycoon.objects.carriage

import scala.collection.mutable.ListBuffer
import tycoon.objects.good._
import tycoon.game.GridLocation
import tycoon.ui.Tile
import tycoon.objects.railway._

class BasicGoodsCarriage extends GoodsCarriage {
  tile = Tile.goods_wagon

  val cost = 10
  val weight = 100
  val max_transport = 100
  var transport = new ListBuffer[Merchandise]
  var current_rail : Option[Rail] = None
  var currentLoc = new GridLocation(-1,-1)
}
