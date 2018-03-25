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
  //Methods
  // protected val _name = StringProperty("")
  // def name : String = _name.value
  // def position : GridLocation = pos

  // intern_time

}
