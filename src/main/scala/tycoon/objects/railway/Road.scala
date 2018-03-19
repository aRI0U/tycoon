package tycoon.objects.railway

import scala.util.Random
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.objects.structure._
import tycoon.ui.Tile
import scala.collection.mutable.{HashMap, ListBuffer}

class Road(pos : GridLocation) {
  var finished : Boolean = false
  var startStructure : Option[Structure] = None
  var endStructure : Option[Structure] = None
  var rails : ListBuffer[Rail] = new ListBuffer
  var length : Int = 0
}
