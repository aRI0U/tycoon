package tycoon.objects.graph

import tycoon.game.{GridLocation, GridRectangle}
import tycoon.ui.Entity
import Array._

class Map(width: Int, height: Int) {

  private var content = Array.fill[Option[Entity]](width, height)(None)

  def add(rect: GridRectangle, e: Entity) = {
    for ((col, row) <- rect.iterate) {
      content(col)(row) = Some(e)
      println("tycoon > objects > graph > Map.scala > addToMap: added element at pos (" + col + ", " + row + ")")
    }
  }

  def remove(rect: GridRectangle) = {
    for ((col, row) <- rect.iterate) {
      content(col)(row) = None
      println("tycoon > objects > graph > Map.scala > addToMap: added element at pos (" + col + ", " + row + ")")
    }
  }

  def isUnused(pos: GridLocation): Boolean =
    content(pos.col)(pos.row) == None

  def isUnused(rect: GridRectangle): Boolean = {
    var bool = true
    for ((col, row) <- rect.iterate) {
      if (content(col)(row) != None)
        bool = false
    }
    bool
  }

  def getContentAt(pos: GridLocation): Option[Entity] = content(pos.col)(pos.row)

}
