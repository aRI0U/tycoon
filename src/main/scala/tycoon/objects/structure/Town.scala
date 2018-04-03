package tycoon.objects.structure

import scala.collection.mutable.ListBuffer
import scala.Array

import tycoon.game.GridLocation
import tycoon.game.{Game, TownManager}
import tycoon.objects.structure._

import tycoon.ui.Tile

import scalafx.beans.property.{IntegerProperty, StringProperty}


abstract class Town(pos: GridLocation, id: Int, townManager: TownManager) extends Structure(pos, id) {
  tile = Tile.town

  protected val r = scala.util.Random
  // names
  var town_names = new ListBuffer[String]
  town_names += ("Paris", "Lyon", "Toulouse", "Saclay", "Nice", "Strasbourg", "Mulhouse", "Aulnay-sous-Bois", "Cachan", "Hamburg", "Berlin", "Brno", "Caderousse","Stuttgart", "Wien", "Köln")

  // choose town name
  def chooseName() {
    try {
      /*val i = r.nextInt(townManager.unchosen_names.length)
      _name.set(townManager.unchosen_names(i))
      townManager.unchosen_names.remove(i)*/
      _name.set(town_names(id))
    }
    catch {
      case e: Exception => println("you've created too many towns")
    }

  }

  chooseName()

  //Booleans about town facilities
  var hasAirport = false
  var hasDock = false
  var airport : Option[Airport]= None
  var dock : Option[Dock]= None

   // _name = StringProperty(city_names(id))
  protected var _population = IntegerProperty(0)
  protected var _waiting_passengers = IntegerProperty(0)
  protected var _jobSeekers = IntegerProperty(0)

  printData += new Tuple2("Name", _name)

  private val populationStr = new StringProperty
  populationStr <== _population.asString
  printData += new Tuple2("Population", populationStr)

  def population : Int = _population.value
  def population_= (new_pop: Int) = _population.set(new_pop)


  private val jobSeekersStr = new StringProperty
  jobSeekersStr <== _jobSeekers.asString
  printData += new Tuple2("Job seekers", jobSeekersStr)

  def jobSeekers : Int = _jobSeekers.value
  def jobSeekers_= (new_seekers: Int) = _jobSeekers.set(new_seekers)

  // gestion of the waiting passengers

  var total_waiters = 0
  var destinations = new ListBuffer[Structure]
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

  // updates

  def updatePopulation () = {
    if (population < max_population) {
      val i = r.nextInt(population)
      population += i/50
    }
  }

  def updateJobSeekers () = {
    if (jobSeekers < population/5) {
      val i = r.nextInt(population)
      jobSeekers += i/100
    }
  }

  def updateWaiters () = {
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
      updatePopulation()
      updateJobSeekers()
      updateWaiters()
      intern_time -= 1
    }
  }

  // def position : GridLocation = pos
  // def name : String = _name.value
  def name_= (new_name: String) = _name.set(new_name)
  def waiting_passengers : Int = _waiting_passengers.value
  def waiting_passengers_= (new_wait_pass: Int) = _waiting_passengers.set(new_wait_pass)
  var max_population: Int = 1000
  population = 50 + r.nextInt(50)
  waiting_passengers = 0
}
