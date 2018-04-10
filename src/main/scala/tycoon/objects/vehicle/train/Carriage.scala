package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.game.{GridLocation, Player}
import tycoon.objects.vehicle._
import scalafx.beans.property._

abstract class Carriage(_id: Int, initialTown: Structure, _owner: Player) extends TrainElement(_id, initialTown, _owner) {
  var stops = new ListBuffer[Structure]
  def owner: Player = _owner

  def update(dt: Double, dirIndicator: Int) = {
    if (visible)
      move(dt, dirIndicator)
  }

  def embark(structure: Structure, stops: ListBuffer[Structure])
  def debark(s: Structure)

  var weight: Double = 200
}
