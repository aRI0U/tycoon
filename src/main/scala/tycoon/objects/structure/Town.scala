package tycoon.objects.structure

import scala.collection.mutable.ListBuffer
import scala.Array

import tycoon.game.GridLocation
import tycoon.game.{Game, TownManager}
import tycoon.objects.structure._

import tycoon.ui.Tile

import scalafx.beans.property.{IntegerProperty, StringProperty}


case class Town(pos: GridLocation, id: Int, townManager: TownManager) extends Structure(pos, id) {
  tile = Tile.town

  protected val r = scala.util.Random

  // choose town name
  def chooseName() {
    try {
      val i = r.nextInt(townManager.unchosen_names.length)
      _name.set(townManager.unchosen_names(i))
      townManager.unchosen_names.remove(i)
    }
    catch {
      case e: Exception => println("you've created too many towns")
    }

  }

  chooseName()


   // _name = StringProperty(city_names(id))
  protected var _population = IntegerProperty(0)
  protected var _waiting_passengers = IntegerProperty(0)

  printData += new Tuple2("Name", _name)

  private val populationStr = new StringProperty
  populationStr <== _population.asString
  printData += new Tuple2("Population", populationStr)

  // private val waitingPassengersStr = new StringProperty
  // waitingPassengersStr <== _waiting_passengers.asString
  // printData += new Tuple2("Waiting passengers", waitingPassengersStr)

  var total_waiters = 0
  var destinations = new ListBuffer[Town]
  var waitersInt = new ListBuffer[IntegerProperty]
  var waitersStr = new ListBuffer[StringProperty]

  def displayWaiters() {
    printData += new Tuple2("Waiting passengers", StringProperty(""))
    for (town <- townManager.towns_list) {
      if (town != this) {
        destinations += town
        waitersInt += IntegerProperty(0)
        waitersStr += new StringProperty
        waitersStr.last <== waitersInt.last.asString
        printData += new Tuple2(town.name, waitersStr.last)
      }
    }
  }

  def waiters(i: Int) : Int = waitersInt(i).value

  def update_population () = {
    if (population < max_population) {
      val i = r.nextInt(population)
      population += i/50
    }
  }

  // to ameliorate to manage where people want to go
  def update_waiters () = {
    try {
      if (total_waiters < population/3) {
        val new_waiters = (r.nextInt(population))/30
        total_waiters += new_waiters
        val destination = r.nextInt(waitersInt.length)
        waitersInt(destination).set(waiters(destination) + new_waiters)
      }
    }
    catch {
      case e: Exception => ()
    }
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
  // def name : String = _name.value
  def name_= (new_name: String) = _name.set(new_name)
  def population : Int = _population.value
  def population_= (new_pop: Int) = _population.set(new_pop)
  def waiting_passengers : Int = _waiting_passengers.value
  def waiting_passengers_= (new_wait_pass: Int) = _waiting_passengers.set(new_wait_pass)
  val max_population: Int = 1000
  population = 50 + r.nextInt(50)
  waiting_passengers = 0
}
