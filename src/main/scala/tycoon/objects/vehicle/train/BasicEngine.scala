package tycoon.objects.vehicle.train


import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.game.{GridLocation, Player}
import scalafx.beans.property._
import tycoon.ui.Tile


class BasicEngine(_owner: Player) extends Engine(_owner) {

  val _thrust = DoubleProperty(200.0) // in tile size percentage per second (ie in tile * 100 / s), here 2tiles/s

  tile = Tile.locomotiveT

}
