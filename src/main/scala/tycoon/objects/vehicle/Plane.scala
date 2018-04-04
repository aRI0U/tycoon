package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import tycoon.game.Game
import tycoon.objects.railway._
import tycoon.objects.structure._
import scalafx.beans.property._
import tycoon.ui.Tile
import tycoon.game.{Game, GridLocation, Player}
import tycoon.ui.DraggableTiledPane


class Plane(id: Int, airport: Structure, val owner: Player) extends Vehicle(id, airport, owner) {
  var location: Structure = airport
  var onTheRoad = BooleanProperty(false)
  tile = Tile.plane
  var speed = DoubleProperty(200.0)
  var weight = 50
  val cost = 500
  gridPos = location match {
    case town: Town => town.gridPos.right
    case struct: Structure => struct.gridPos
  }
}
