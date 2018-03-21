package tycoon.objects.structure

import tycoon.objects.good._
import tycoon.game.GridLocation


abstract class Facility(pos: GridLocation, id: Int) extends Structure(pos, id) {
  val production_time : Double = 1
  var intern_time : Double = 0
  var stock : Int = 0

  var product : Good = new Iron
  var production_per_period : Int = 1
  var production_price : Int = 1


  def update_production() {
    stock += production_per_period
  }

  def update(dt: Double) = {
    intern_time += dt
    if (intern_time > production_time) {
      update_production()
      intern_time -= production_time
    }
  }
}
