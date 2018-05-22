package tycoon.game


import scalafx.Includes._
import scalafx.beans.property.{StringProperty, IntegerProperty}
import scalafx.beans.binding.Bindings

import tycoon.objects.vehicle._
import tycoon.objects.vehicle.train._

class Player {
  private val formatter = java.text.NumberFormat.getIntegerInstance

  private val _name: StringProperty = StringProperty("You")
  private val _money: IntegerProperty = IntegerProperty(0)
  private val _formattedMoney = StringProperty("0")

  _money.onChange { _formattedMoney.set(formatter.format(_money.value)) }

  def name : StringProperty = _name
  def name_= (new_name: String) = _name.set(new_name)

  def money : IntegerProperty = _money
  def money_=(new_money: Int) = _money.set(new_money)
  def formattedMoney: StringProperty = _formattedMoney
  money_=(1000000)

  // for charts
  private var currentV: Option[Vehicle] = None

  private var _planeProfits: Int = 0
  private var _boatProfits: Int = 0
  private var _trainProfits: Int = 0
  private var _planeExpenses: Int = 0
  private var _boatExpenses: Int = 0
  private var _trainExpenses: Int = 0

  def planeProfits: Int = _planeProfits
  def boatProfits: Int = _boatProfits
  def trainProfits: Int = _trainProfits
  def planeExpenses: Int = _planeExpenses
  def boatExpenses: Int = _boatExpenses
  def trainExpenses: Int = _trainExpenses

  def pay(price: Int): Boolean = {
    if (money.value >= price) {
      currentV match {
        case None => ()
        case Some(v) => v match {
          case _: Plane => _planeExpenses += price
          case _: Boat => _boatExpenses += price
          case _: PassengerCarriage | _: Train => _trainExpenses += price
          case _ => ()
        }
      }
      _money.set(_money.value - price) ; true
    }
    else false
  }
  def earn(amount: Int) = {
    currentV match {
      case None => ()
      case Some(v) => v match {
        case _: Plane => _planeProfits += amount
        case _: Boat => _boatProfits += amount
        case _: PassengerCarriage | _: Train => _trainProfits += amount
        case _ => ()
      }
    }
    _money.set(_money.value + amount)
  }

  def canAffordPaying(amount: Int): Boolean = money.value >= amount

  def setCurrentVehicle(v: Vehicle): Unit = currentV = Some(v)
}
