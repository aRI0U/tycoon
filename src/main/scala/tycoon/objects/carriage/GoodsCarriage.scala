package tycoon.objects.carriage

import scala.collection.mutable.ListBuffer

import tycoon.objects.carriage._
import tycoon.objects.structure._
import tycoon.objects.good._
import tycoon.ui.Renderable
import tycoon.ui.Tile
import tycoon.game.GridLocation
import tycoon.objects.railway._


case class GoodsCarriage() extends Carriage {

  // override def embark(facility: Facility) : Unit = {
  // }
  //
  // override def debark(facility: Facility) = {
  // }


  tile = Tile.goodsWagonR

  val cost = 10
  val weight = 100
  val max_transport = 100
  var transport = new ListBuffer[Merchandise]
  var current_rail : Option[Rail] = None
  var currentLoc = new GridLocation(-1,-1)
}
