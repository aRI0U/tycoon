package tycoon.objects.structure

import scala.collection.mutable.ListBuffer
import scala.Array

import tycoon.game.{GridLocation, Player}
import tycoon.game.{Game, TownManager}
import tycoon.objects.structure._
import tycoon.objects.good._

import tycoon.ui.{PrintableData, PrintableElement}
import tycoon.ui.Tile

import scalafx.beans.property.{BooleanProperty, DoubleProperty, IntegerProperty, StringProperty}


abstract class Town(pos: GridLocation, id: Int, townManager: TownManager, override val owner: Player) extends EconomicAgent(pos, id, townManager, owner) {

  tile = Tile.Town

  // choose town name
  def chooseName() {
    try {
      val nameId = r.nextInt(townManager.townNames.length)
      val name = StringProperty(townManager.townNames(nameId))
      setName(townManager.townNames(nameId))
      townManager.townNames -= townManager.townNames(nameId)
    }
    catch {
      case e: Exception => {
        _name.set("random name")
      }
    }
  }

  chooseName()

  printData += new PrintableData(name + " (" + owner.name.value + ")")
  printData += new PrintableData("Products")
  printData += new PrintableData("Waiting passengers")

  var hasAirport = false
  var hasDock = false
  var airport : Option[Airport]= None
  var dock : Option[Dock]= None

  protected var _population = IntegerProperty(0)
  protected var _waiting_passengers = IntegerProperty(0)
  protected var _jobSeekers = IntegerProperty(0)

  private val populationStr = new StringProperty
  populationStr <== _population.asString
  printData(0).data += new PrintableElement("Population", _population)

  def population : Int = _population.value
  def population_= (new_pop: Int) = _population.set(new_pop)


  private val jobSeekersStr = new StringProperty
  jobSeekersStr <== _jobSeekers.asString
  printData(0).newElement("Job seekers", _jobSeekers)

  def jobSeekers : Int = _jobSeekers.value
  def jobSeekers_= (new_seekers: Int) = _jobSeekers.set(new_seekers)

  // gestion of the waiting passengers

  var totalWaiters = 0
  var destinations = new ListBuffer[Structure]
  var waitersInt = new ListBuffer[IntegerProperty]

  def displayWaiters() {
    for (town <- townManager.townsList) {
      if (town != this) {
        destinations += town
        waitersInt += IntegerProperty(0)
        printData(2).newRankedElement(town.name, waitersInt.last)
      }
    }
  }

  def waiters(i: Int) : Int = {
    if (i >= 0 && i < waitersInt.length)
      waitersInt(i).value
    else -1
  }

  def updatePopulation () = {
    if (population < max_population) {
      val i = r.nextInt(population)
      population += i/80
    }
  }

  def updateJobSeekers () = {
    if (jobSeekers < population/5) {
      val i = r.nextInt(population)
      jobSeekers += i/100
    }
    else {
      if (jobSeekers > population/2) {
        _jobSeekers.set(population/3)
      }
    }
  }

  def updateWaiters () = {
    try {
      if (totalWaiters < population/3) {
        val new_waiters = (r.nextInt(population))/30
        val destination = r.nextInt(waitersInt.length)
        waitersInt(destination).set(waiters(destination) + new_waiters)
        totalWaiters += new_waiters
      }
      else {
        // if people are dying for any reason
        if (totalWaiters > population/2) {
          val diedWaiters = totalWaiters - population/2
          var i = 0
          while (totalWaiters > population/2) {
            try {
              if (waiters(i) > 0) {
                var newDeads = waiters(i).min(diedWaiters/5+1)
                waitersInt(i).set((waiters(i) - newDeads))
                totalWaiters -= newDeads
              }
              i += 1
            } catch {
              case e: IndexOutOfBoundsException => i = 0
            }
          }
        }
      }
    }
    catch {
      case e: IllegalArgumentException => ()
    }
  }

  var alive = true

  def reInit() = {
    population = minPopulation
    alive = false
    jobSeekers = minPopulation/2
    totalWaiters = 0
    waitersInt.foreach(_.set(0))
    hunger = 0
    for (i <- 0 to stock.requestsInt.length-1) stock.requestsInt(i).set(0)
    throwEvent("["+name+"] Everyone is dead here...")
  }

  def update(dt: Double) = {
    if (alive) {
      internTime += dt
      if (internTime > 2 && population > 0) {
        internTime -= 2
        updatePopulation()
        updateJobSeekers()
        updateWaiters()
        updateConsumption()
        if (stock.updateExpiredProducts(townManager.getTime())) throwEvent("["+name+"] Be careful! Some of your food is expiring!")
        updateEconomy()
        newRandomRequest(4)
      }
      if (population <= minPopulation) {
        reInit()
      }
    }
  }

  def waiting_passengers : Int = _waiting_passengers.value
  def waiting_passengers_= (new_wait_pass: Int) = _waiting_passengers.set(new_wait_pass)

  var minPopulation = 0
  var max_population: Int = 1000
  population = 50 + r.nextInt(50)
  waiting_passengers = 0

  // requests of the town

  def newRequest(good: Good, amount: Int) = {
    stock.newProduct(good, -amount)
  }

  // hunger

  var hunger = 0
  var alreadyDiet = false
  var alreadyStarving = false
  val lunchTime = 25
  val dietTime = 100
  val starvingTime = 400
  val nutritiousNeeds = 1

  def feedPopulation(needs: Double) : Double = {
    var i = 0
    var totalNeeds = needs
    while (totalNeeds > 0 && i < stock.datedProducts.length) {
      stock.productsTypes(i) match {
        case f: Food => {
          while (totalNeeds > 0 && stock.datedProducts(i).length > 0) {
            var m = stock.datedProducts(i)(0)
            totalNeeds -= m.quantity*f.nutritiousness
            stock.datedProducts(i).remove(0)
          }
        }
        case _ => ()
      }
      i += 1
    }
    totalNeeds
  }

  def newRandomRequest(i : Int) {
    // newRequest(Product.Iron, 5)
    if (i == 0) {
      val food = Product.foods(scala.util.Random.nextInt(Product.foods.size))
      newRequest(food, ((feedPopulation(population*nutritiousNeeds)/(5*food.nutritiousness)).toInt))
    }
    if (i == 1) {
      val purchase = Product.purchases(0)//scala.util.Random.nextInt(Product.purchases.size))
      newRequest(purchase,(population/(purchase.price.value)).toInt)
    }
  }

  def updateConsumption() = {
    val needs = population*nutritiousNeeds
    if (scala.util.Random.nextInt(50) == 1 && (needs > (dietTime/6))) newRandomRequest(0)
    if (scala.util.Random.nextInt(70) == 1) newRandomRequest(1)
    val stayingNeeds = feedPopulation(needs)
    hunger = (hunger*stayingNeeds/needs).toInt
    if (hunger > dietTime) {
      if (hunger > starvingTime) {
        population -= hunger*2
        if (!alreadyStarving) {
          throwEvent("["+name+"] People are starving, nur ein Gott kann sie retten...")
          newRequest(Product.Corn, ((stayingNeeds/(5*Product.Corn.nutritiousness)).toInt))
        }
        alreadyStarving = true
      }
    }
    hunger += 1
  }

}
