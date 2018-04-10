package tycoon.objects.structure

import scala.collection.mutable.ListBuffer

import tycoon.game._
import tycoon.objects.good._
import tycoon.ui.{PrintableData, PrintableElement}

import scalafx.beans.property.{BooleanProperty, DoubleProperty, IntegerProperty, StringProperty}

abstract class Facility(pos: GridLocation, id: Int, townManager: TownManager) extends EconomicAgent(pos, id, townManager) {

  // workers
  protected var _workers = IntegerProperty(0)

  private val workersStr = new StringProperty
  workersStr <== _workers.asString

  printData += new PrintableData("")
  printData += new PrintableData("Products")
  printData(0).data += new PrintableElement("Workers", _workers)

  def workers : Int = _workers.value
  def workers_= (new_workers: Int) = _workers.set(new_workers)
}
