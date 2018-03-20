package tycoon.objects.structure

import scala.collection.mutable.ListBuffer

import tycoon.objects.good._
import tycoon.game.GridLocation

import scalafx.beans.property.{IntegerProperty, StringProperty}

abstract class Facility(pos: GridLocation, id: Int) extends Structure(pos, id) {
  var products = new ListBuffer[Good]
  var intern_time : Double = 0

  for (i <- products) {

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
