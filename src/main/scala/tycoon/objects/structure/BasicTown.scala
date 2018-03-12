package tycoon.objects.structure

import scala.util.Random

import tycoon.GridLocation
import tycoon.ui.Tile

import scalafx.beans.property.IntegerProperty

class BasicTown(pos: GridLocation, id: Int) extends Town(pos: GridLocation, id: Int) {
  population = 50 + r.nextInt(50)
  waiting_passengers = 0

  val tile = new Tile(Tile.town)
  gridLoc = pos
}
