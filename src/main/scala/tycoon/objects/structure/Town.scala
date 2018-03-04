package tycoon.objects.structure

import tycoon.GridLocation


abstract class Town(pos: GridLocation) extends Structure(pos) {

  protected val r = scala.util.Random

  private val _name = "Jeanne"
  protected var _population : Int
  private var waiting_passengers : Int = 0

  private var intern_time : Double = 0
  def update_population (dt: Double) = {
    intern_time += dt
    if (intern_time > 1) {
      for (i <- 0 to _population) // faut opti ça sinon ca fait 1000000 tours de boucle par seconde
        if (r.nextInt(100) == 0)
          _population += 1
      intern_time -= 1
    }
  }


  def update(dt: Double) = {
    update_population(dt)
  }


  def name : String = _name
  def population : Int = _population


}
