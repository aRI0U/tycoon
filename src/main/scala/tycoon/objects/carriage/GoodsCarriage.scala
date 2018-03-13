package tycoon.objects.carriage

import tycoon.GridLocation
import tycoon.ui.Tile
import tycoon.objects.railway._

class GoodsCarriage extends Carriage {
  val cost = 20
  val ticket_price = 3
  val weight = 100
  val max_passengers = 10
  var passengers = 0
  val tile = new Tile(Tile.goods_wagon)
  var current_rail : Option[Rail] = None
  gridLoc = new GridLocation(-1,-1)
}
