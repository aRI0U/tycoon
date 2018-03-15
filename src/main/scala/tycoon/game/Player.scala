package tycoon.game


import scalafx.Includes._
import scalafx.beans.property.{StringProperty, IntegerProperty}
import scalafx.beans.binding.Bindings

class Player {
  private var _name: StringProperty = StringProperty("")
  private var _money: IntegerProperty = IntegerProperty(0)

  def name : StringProperty = _name
  def name_= (new_name: String) = _name.set(new_name)

  def money : IntegerProperty = _money
  def money_= (new_money: Int) = _money.set(new_money)
}
