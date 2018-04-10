package tycoon.objects.graph

import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.ui.Tile
import tycoon.objects.vehicle.train._
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle._
import tycoon.game.Game
import tycoon.game.GridLocation



class Trip(var origin: Structure, var destination: Structure, val vehicle: Vehicle, var repeated: Boolean) {

  def active: Boolean = true // tmp

  def start() = {
    
  }

  def update (dt: Double) {

  }
}
