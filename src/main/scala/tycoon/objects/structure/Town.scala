package tycoon.objects.structure

import tycoon.GridLocation


abstract class Town(pos: GridLocation) extends Structure(pos) {

  protected val r = scala.util.Random

  private val name = "Jeanne"
  protected var population : Integer
  private var waiting_passengers : Integer = 0

  private var intern_time : Double = 0
  def update_population (dt: Double) {
    intern_time += dt
    if (intern_time > 1) {
      for (i <- 0 to population) // ???
        if (r.nextInt(100) == 0)
          population += 1
      intern_time -= 1
    }
  }


}
