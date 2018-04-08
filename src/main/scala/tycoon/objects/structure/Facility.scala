package tycoon.objects.structure

import scala.collection.mutable.ListBuffer

import tycoon.objects.good._
import tycoon.game._

import scalafx.beans.property.{IntegerProperty, StringProperty}

abstract class Facility(pos: GridLocation, id: Int, val townManager: TownManager) extends Structure(pos, id) {

  // workers
  protected var _workers = IntegerProperty(0)

  private val workersStr = new StringProperty
  workersStr <== _workers.asString

  printData += new PrintableData("")
  printData += new PrintableData("Products")
  printData(0).data += new Tuple2("Workers", workersStr)

  def workers : Int = _workers.value
  def workers_= (new_workers: Int) = _workers.set(new_workers)

  def displayProducts() {
    // for (p <- products) {
    //   stocksInt += IntegerProperty(0)
    //   stocksStr += new StringProperty
    //   stocksStr.last <== stocksInt.last.asString
    //   printData += new Tuple2(p.label, stocksStr.last)
    // }
  }
}
