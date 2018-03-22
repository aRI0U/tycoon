package tycoon.objects.structure

import scala.collection.mutable.ListBuffer
import scala.Array

import tycoon.game.GridLocation
import tycoon.game.Game

import scalafx.beans.property.{IntegerProperty, StringProperty}


abstract case class Town(pos: GridLocation, id: Int) extends Structure(pos, id) {

  protected val r = scala.util.Random

  val max_population : Int

  var city_names : ListBuffer[String] = new ListBuffer
  city_names += ("Paris", "Lyon", "Toulouse", "Saclay", "Nice", "Strasbourg", "Mulhouse", "Aulnay-sous-Bois", "Cachan", "Hamburg", "Berlin", "Brno", "Caderousse","Stuttgart", "Wien", "Köln")

   // _name = StringProperty(city_names(id))
  protected var _population = IntegerProperty(0)
  protected var _waiting_passengers = IntegerProperty(0)

  printData += new Tuple2("Name", _name)

  private val populationStr = new StringProperty
  populationStr <== _population.asString
  printData += new Tuple2("Population", populationStr)

  private val waitingPassengersStr = new StringProperty
  waitingPassengersStr <== _waiting_passengers.asString
  printData += new Tuple2("Waiting passengers", waitingPassengersStr)


  def update_population () = {
    if (population < max_population) {
      val i = r.nextInt(population)
      population += i/50
    }

  }

  // to ameliorate to manage where people want to go
  def update_waiters () = {
    val new_waiters = (r.nextInt(population))/30
    waiting_passengers += new_waiters
    if (waiting_passengers > population/3) waiting_passengers = population/3
  }

  override def update(dt: Double) = {
    intern_time += dt
    if (intern_time > 1) {
      update_population()
      update_waiters()
      intern_time -= 1
    }
  }

  // def position : GridLocation = pos
  // def name : String = _name.get()
  def name_= (new_name: String) = _name.set(new_name)
  def population : Int = _population.get()
  def population_= (new_pop: Int) = _population.set(new_pop)
  def waiting_passengers : Int = _waiting_passengers.get()
  def waiting_passengers_= (new_wait_pass: Int) = _waiting_passengers.set(new_wait_pass)


}
