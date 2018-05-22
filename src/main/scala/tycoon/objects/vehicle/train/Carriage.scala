package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.game.{GridLocation, Player}
import tycoon.objects.vehicle._
import scalafx.beans.property._

abstract class Carriage(_id: Int, initialTown: Structure, override val owner: Player) extends TrainElement(_id, initialTown, owner) {
  var stops = new ListBuffer[Structure]

  override def update(dt: Double, dirIndicator: Int) = {
    super.update(dt, dirIndicator)
    if (visible)
      move(dt, dirIndicator)
  }

  def embark(structure: Structure, stops: ListBuffer[Structure])
  def debark(s: Structure)

  var weight: Double = 200
  var consumption = 0
}
