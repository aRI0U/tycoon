package tycoon.objects.landscape

import scala.collection.mutable.ListBuffer

import tycoon.objects.vehicle._
import tycoon.ui.Renderable
import tycoon.ui.Tile
import tycoon.game.GridLocation

import scalafx.beans.property.{IntegerProperty, StringProperty}


class Tree (pos: GridLocation, id: Int) extends Renderable(pos) {
  val treeNumber = id
  tile = Tile.tree
}
