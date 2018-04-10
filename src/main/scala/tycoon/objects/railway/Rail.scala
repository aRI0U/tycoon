package tycoon.objects.railway

import scala.util.Random
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile
import tycoon.ui.Renderable
import scalafx.beans.property.{IntegerProperty, StringProperty}

case class Rail(pos: GridLocation) extends RoadItem(pos) {

  var road = new Road
  road.length = 1
  road.rails += this

  var next : Rail = this
  var previous : Rail = this

  // -1: undefined ; 0: top ; 1: right ; 2: bottom ; 3: left
  var previousDir: Int = -1
  var nextDir: Int = -1

  def nextInDir(dir: Int) : Rail = {
    if (dir == 0) next
    else previous
  }

  tile = Tile.StraightRailBT 

  val max_speed = 50
  val max_weight = 1000
}
