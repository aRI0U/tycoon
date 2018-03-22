package tycoon.objects.structure

import scala.collection.mutable.ListBuffer

import tycoon.objects.good._
import tycoon.game.GridLocation

import scalafx.beans.property.{IntegerProperty, StringProperty}

abstract class Facility(pos: GridLocation, id: Int) extends Structure(pos, id) {

  // workers
  protected var _workers = IntegerProperty(0)

  private val workersStr = new StringProperty
  workersStr <== _workers.asString
  printData += new Tuple2("Prolos de merde", workersStr)

  def workers : Int = _workers.get()
  def workers_= (new_workers: Int) = _workers.set(new_workers)


  var products : ListBuffer[Good]
  var stocks = new ListBuffer[IntegerProperty]
  var stocksStr = new ListBuffer[StringProperty]

  def displayProducts() {
    println(products)
    for (p <- products) {
      stocks += IntegerProperty(0)
      stocksStr += new StringProperty
      stocksStr.last <== stocks.last.asString
      printData += new Tuple2(p.label, stocksStr.last)
    }
    println(products)
  }

  // val product : Good
  // var production_per_period : Int
  // var production_price : Int


  // def update_production(good: Good) = {
  //   stock += production_per_period
  // }
  //
  // def update(dt: Double) = {
  //   intern_time += dt
  //   if (intern_time > production_time) {
  //     update_production()
  //     intern_time -= production_time
  //   }
  // }
}
