package tycoon.objects.structure

import scala.util.Random
import scala.collection.mutable.ListBuffer

import tycoon.game.{GridLocation,Player}
import tycoon.game.Game

import tycoon.objects.vehicle.Plane

import tycoon.ui.Tile

case class Airport(pos: GridLocation, id: Int, owner: Player) extends Structure(pos, id, owner) {
  tile = Tile.Airport
  var dependanceTown : Option[Town] = None
  setName("Airport " + id.toString)
  def update(dt: Double) = {

  }
}
