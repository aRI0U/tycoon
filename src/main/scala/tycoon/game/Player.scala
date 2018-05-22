package tycoon.game

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._

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

  val towns = new ListBuffer[Town]
  val facilities = new ListBuffer[Facility]
  val farms = new ListBuffer[Farm]
  val docks = new ListBuffer[Dock]
  val airports = new ListBuffer[Airport]

  _money.onChange { _formattedMoney.set(formatter.format(_money.value)) }

  def name : StringProperty = _name
  def name_= (new_name: String) = _name.set(new_name)

  def money : IntegerProperty = _money
  def money_=(new_money: Int) = _money.set(new_money)
  def formattedMoney: StringProperty = _formattedMoney
  money_=(50000)

  // for charts
  private var currentV: Option[Vehicle] = None

  private var _planeProfits: Int = 0
  private var _boatProfits: Int = 0
  private var _trainProfits: Int = 0
  private var _planeExpenses: Int = 0
  private var _boatExpenses: Int = 0
  private var _trainExpenses: Int = 0
  private var _structSpendings: Int = 0
  private var _roadSpendings: Int = 0
  private var _vehicleSpendings: Int = 0
  private var _nbStructuresBuilt: Int = 0
  private var _nbRoadsBuilt: Int = 0
  private var _nbVehiclesBought: Int = 0

  def planeProfits: Int = _planeProfits
  def boatProfits: Int = _boatProfits
  def trainProfits: Int = _trainProfits
  def planeExpenses: Int = _planeExpenses
  def boatExpenses: Int = _boatExpenses
  def trainExpenses: Int = _trainExpenses
  def structSpendings: Int = _structSpendings
  def roadSpendings: Int = _roadSpendings
  def vehicleSpendings: Int = _vehicleSpendings
  def nbStructuresBuilt: Int = _nbStructuresBuilt
  def nbRoadsBuilt: Int = _nbRoadsBuilt
  def nbVehiclesBought: Int = _nbVehiclesBought

  private val initialMoney = money.value
  var _moneyMonitoring: ListBuffer[Int] = ListBuffer(0)
  var _trainsMoneyMonitoring: ListBuffer[Int] = ListBuffer(0)
  var _boatsMoneyMonitoring: ListBuffer[Int] = ListBuffer(0)
  var _planesMoneyMonitoring: ListBuffer[Int] = ListBuffer(0)

  def moneyMonitoring: ListBuffer[Int] = _moneyMonitoring
  def trainsMoneyMonitoring: ListBuffer[Int] = _trainsMoneyMonitoring
  def boatsMoneyMonitoring: ListBuffer[Int] = _boatsMoneyMonitoring
  def planesMoneyMonitoring: ListBuffer[Int] = _planesMoneyMonitoring

  def tick(): Unit = {
    moneyMonitoring.append(money.value - initialMoney)
    trainsMoneyMonitoring.append(trainProfits - trainExpenses)
    boatsMoneyMonitoring.append(boatProfits - boatExpenses)
    planesMoneyMonitoring.append(planeProfits - planeExpenses)
  }

  def pay(price: Int, ty: Int): Boolean = {
    if (money.value >= price) {
      ty match {
        case 0 => { _structSpendings += price ; _nbStructuresBuilt += 1 }
        case 1 => { _roadSpendings += price ; _nbRoadsBuilt += 1 }
        case 2 => { _vehicleSpendings += price ; _nbVehiclesBought += 1 }
        case 3 =>
          currentV match {
            case None => ()
            case Some(v) => v match {
              case _: Plane => _planeExpenses += price
              case _: Boat => _boatExpenses += price
              case _: PassengerCarriage | _: Train => _trainExpenses += price
              case _ => ()
            }
          }
        case _ => ()
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


  def get(s: Structure) = {
    s match {
      case a: Airport => airports += a
      case d: Dock => docks += d
      case f: Farm => farms += f
      case f: Facility => facilities += f
      case t: Town => towns += t
    }
  }

  def setCurrentVehicle(v: Vehicle): Unit = currentV = Some(v)

}
