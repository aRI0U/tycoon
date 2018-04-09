package tycoon.game

import scala.collection.mutable.ListBuffer

import scalafx.beans.property.{BooleanProperty, StringProperty}

class PrintableData(s: String) {
  val label = s
  val data = new ListBuffer[(String, StringProperty, BooleanProperty)]

  def newData(name: String, property: StringProperty) = {
    var visible = new BooleanProperty
    visible <== property =!= StringProperty("0")
    data += new Tuple3(name, property, visible)
  }
}
