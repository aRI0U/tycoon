package tycoon.game

import scala.collection.mutable.ListBuffer

import scalafx.beans.property.StringProperty

class PrintableData(s: String) {
  val label = s
  val data = new ListBuffer[(String, StringProperty)]
}
