package tycoon.objects.vehicle.train


import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.game.{GridLocation, Player}
import scalafx.beans.property._




abstract class Engine(_owner: Player) extends Renderable(new GridLocation(-1, -1)) {
  var currentRail: Option[Rail] = None
  def owner: Player = _owner


  protected val _thrust: DoubleProperty
  def thrust: DoubleProperty = _thrust
  def thrust_=(newThrust: Double) = _thrust.set(newThrust)

}
