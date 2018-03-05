package tycoon.objects.structure

import tycoon.GridLocation
import scalafx.beans.property.{IntegerProperty, StringProperty}



abstract class Town(pos: GridLocation) extends Structure(pos) {

  protected val r = scala.util.Random

  protected val _name = StringProperty("Jeanne")
  protected var _population = IntegerProperty(0)
  protected var _waiting_passengers = IntegerProperty(0)


  printData += Pair("Name", _name)

  private val populationStr = new StringProperty
  populationStr <== _population.asString
  printData += Pair("Population", populationStr)

  private val waitingPassengersStr = new StringProperty
  waitingPassengersStr <== _waiting_passengers.asString
  printData += Pair("Waiting passengers", waitingPassengersStr)


  private var intern_time : Double = 0
  def update_population (dt: Double) = {
    intern_time += dt
    if (intern_time > 1) {
      for (i <- 0 to population) // faut opti ça sinon ca fait 1000000 tours de boucle par seconde
        if (r.nextInt(100) == 0)
          population += 1
      intern_time -= 1
    }
  }


  def update(dt: Double) = {
    update_population(dt)
  }


  def name : String = _name.get()
  def name_= (new_name: String) = _name.set(new_name)
  def population : Int = _population.get()
  def population_= (new_pop: Int) = _population.set(new_pop)
  def waiting_passengers : Int = _waiting_passengers.get()
  def waiting_passengers_= (new_wait_pass: Int) = _waiting_passengers.set(new_wait_pass)


}
