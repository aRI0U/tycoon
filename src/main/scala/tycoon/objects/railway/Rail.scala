package tycoon.objects.railway

import scala.util.Random
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile
import tycoon.ui.Renderable
import scalafx.beans.property.{IntegerProperty, StringProperty}

abstract class Rail(pos: GridLocation, tile_type: Int) extends Renderable(pos) {
  val cost : Int
  val max_speed : Int
  val max_weight : Int
  var road_head : Boolean
  var nb_rotation : Int


  var road = new Road(pos)
  road.length+=1
  road.rails += this

  var next : Rail = this
  var previous : Rail = this
  var origin = 0
  var orientation = 0

  def direction(i : Int) : Rail = {
    if (i==0) return next
    else return previous
  }

  def position : GridLocation = pos
  def get_tile_type : Int
  def get_rotation : Int = nb_rotation
}
