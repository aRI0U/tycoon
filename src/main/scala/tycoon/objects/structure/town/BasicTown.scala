package tycoon.objects.structure

import scala.util.Random

import tycoon.game.GridLocation
import tycoon.ui.Tile

import scalafx.beans.property.IntegerProperty

class BasicTown(pos: GridLocation, id: Int) extends Town(pos, id) {
  tile = Tile.town

  val max_population = 1000
  population = 50 + r.nextInt(50)
  waiting_passengers = 0
}
