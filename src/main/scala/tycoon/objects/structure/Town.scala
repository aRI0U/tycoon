package tycoon.objects.structure

import tycoon.GridLocation
import scalafx.beans.property.{IntegerProperty, StringProperty}



abstract class Town(pos: GridLocation, id: Int) extends Structure(pos, id) {

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
  def update_population () = {
    val i = r.nextInt(population)
    population += i/50
  }

  def update_waiters () = {
    val i = r.nextInt(population)
    waiting_passengers += i/30
    if (waiting_passengers > population) waiting_passengers = population
  }

  def update(dt: Double) = {
    intern_time += dt
    if (intern_time > 1) {
      update_population()
      update_waiters()
      intern_time -= 1
    }
  }

  def position : GridLocation = pos
  def name : String = _name.get()
  def name_= (new_name: String) = _name.set(new_name)
  def population : Int = _population.get()
  def population_= (new_pop: Int) = _population.set(new_pop)
  def waiting_passengers : Int = _waiting_passengers.get()
  def waiting_passengers_= (new_wait_pass: Int) = _waiting_passengers.set(new_wait_pass)


}
