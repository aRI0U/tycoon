package tycoon.game


import scalafx.Includes._
import scalafx.beans.property.{StringProperty, IntegerProperty}
import scalafx.beans.binding.Bindings

class Player {
  private val formatter = java.text.NumberFormat.getIntegerInstance

  private val _name: StringProperty = StringProperty("")
  private val _money: IntegerProperty = IntegerProperty(0)
  private val _formattedMoney = StringProperty("0")

  _money.onChange { _formattedMoney.set(formatter.format(_money.value)) }

  def name : StringProperty = _name
  def name_= (new_name: String) = _name.set(new_name)

  def money : IntegerProperty = _money
  def money_=(new_money: Int) = _money.set(new_money)
  def formattedMoney: StringProperty = _formattedMoney

  def pay(price: Int): Boolean = {
    if (money.value >= price) { _money.set(_money.value - price) ; true }
    else false
  }
  def earn(amount: Int) = _money.set(_money.value + amount)

  def canAffordPaying(amount: Int): Boolean = money.value >= amount
}
