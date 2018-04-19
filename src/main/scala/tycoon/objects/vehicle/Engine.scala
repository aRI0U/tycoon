package tycoon.objects.vehicle


import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.game.{GridLocation, Player}
import scalafx.beans.property._



class Engine(_owner: Player) extends Renderable(new GridLocation(-1, -1)) {
  var currentRail: Option[Rail] = None
  def owner: Player = _owner

  val _thrust = DoubleProperty(200.0)
  def thrust: DoubleProperty = _thrust
  def thrust_=(newThrust: Double) = _thrust.set(newThrust)

  private var _upgradeLevel = IntegerProperty(0)
  def upgradeLevel: IntegerProperty = _upgradeLevel

  var crusadeDistance = 10

  def upgrade(): Boolean = {
    if (_upgradeLevel.value < Engine.MaxUpgradeLevel && owner.pay(Engine.Price(upgradeLevel.value + 1))) {
      upgradeLevel.set(upgradeLevel.value + 1)
      thrust.set(Engine.Thrust(upgradeLevel.value))
      crusadeDistance = Engine.CrusadeDistance(upgradeLevel.value)
      true
    }
    else false
  }
}


object Engine {
  val MaxUpgradeLevel: Int = 5

  val Thrust = Array(200, 300, 400, 500, 600, 700)
  val CrusadeDistance = Array(10, 8, 6, 4, 2, 1)
  val Price = Array(100, 200, 300, 400, 500, 600)
}
