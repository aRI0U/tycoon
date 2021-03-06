package tycoon.objects.vehicle


import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.objects.vehicle.train._
import tycoon.game.{GridLocation, Player, Settings}
import scalafx.beans.property.{DoubleProperty,IntegerProperty}



class Engine(_owner: Player, kind: Vehicle) extends Renderable(new GridLocation(-1, -1)) {
  var currentRail: Option[Rail] = None
  def owner: Player = _owner

  val _thrust = DoubleProperty(0.0)
  def thrust: DoubleProperty = {
    kind match {
      case train: TrainElement => thrust = Engine.Thrust(upgradeLevel.value)
      case boat: Boat => thrust = Settings.BoatSpeed
      case plane: Plane => thrust = Settings.PlaneSpeed
      case truck: Truck => thrust = Settings.TruckSpeed
      case _ => ()
    }
    _thrust
  }

  def thrust_=(newThrust: Double) = _thrust.set(newThrust)

  private var _upgradeLevel = IntegerProperty(0)
  def upgradeLevel: IntegerProperty = _upgradeLevel

  def upgrade(): Boolean = {
    if (_upgradeLevel.value < Engine.MaxUpgradeLevel && owner.pay(Engine.Price(upgradeLevel.value + 1), 2)) {
      upgradeLevel.set(upgradeLevel.value + 1)
      thrust.set(Engine.Thrust(upgradeLevel.value))
      true
    }
    else false
  }
}


object Engine {
  val MaxUpgradeLevel: Int = 5

  val Thrust = Array(200, 300, 400, 500, 600, 700)
  val Price = Array(100, 200, 300, 400, 500, 600)
}
