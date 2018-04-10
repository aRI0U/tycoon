package tycoon.objects.structure

import scala.util.Random
import scala.collection.mutable.ListBuffer

import tycoon.game.GridLocation
import tycoon.game.Game

import tycoon.objects.vehicle.Plane

import tycoon.ui.Tile

case class Airport(pos: GridLocation, id: Int) extends Structure(pos, id) {
  tile = Tile.airport
  var dependanceTown : Option[Town] = None
  setName("Airport " + id.toString)
  def update(dt: Double) = {
  }
}
