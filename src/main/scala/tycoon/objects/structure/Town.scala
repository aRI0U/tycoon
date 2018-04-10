package tycoon.objects.structure

import scala.collection.mutable.ListBuffer
import scala.Array

import tycoon.game.GridLocation
import tycoon.game.{Game, TownManager}
import tycoon.objects.structure._
import tycoon.objects.good._

import tycoon.ui.{PrintableData, PrintableElement}
import tycoon.ui.Tile

import scalafx.beans.property.{BooleanProperty, DoubleProperty, IntegerProperty, StringProperty}


abstract class Town(pos: GridLocation, id: Int, townManager: TownManager) extends EconomicAgent(pos, id, townManager) {

  tile = Tile.town

  // choose town name
  def chooseName() {
    try {
      /*val i = r.nextInt(townManager.unchosen_names.length)
      _name.set(townManager.unchosen_names(i))
      townManager.unchosen_names.remove(i)*/
      val nameId = r.nextInt(townManager.townNames.length)
      val name = StringProperty(townManager.townNames(nameId))
      setName(townManager.townNames(nameId))
      townManager.townNames -= townManager.townNames(nameId)
    }
    catch {
      case e: Exception => {
        _name.set("random name")
        println("you've created too many towns")
      }
    }
  }

  chooseName()

  printData += new PrintableData(name)
  printData += new PrintableData("Products")
  printData += new PrintableData("Waiting passengers")

  //Booleans about town facilities
  var hasAirport = false
  var hasDock = false
  var airport : Option[Airport]= None
  var dock : Option[Dock]= None

   // _name = StringProperty(city_names(id))
  protected var _population = IntegerProperty(0)
  protected var _waiting_passengers = IntegerProperty(0)
  protected var _jobSeekers = IntegerProperty(0)

  //printData += new Tuple2("Name", _name)

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
    //printData += new Tuple2("Waiting passengers", StringProperty(""))
    for (town <- townManager.townsList) {
      if (town != this) {
        destinations += town
        waitersInt += IntegerProperty(0)
        printData(2).newRankedElement(town.name, waitersInt.last)
      }
    }
  }

  def waiters(i: Int) : Int = waitersInt(i).value

  // updates

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
        println("seekers will die")
        _jobSeekers.set(population/3)
        println("seekers died")
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
          println("waiters will die")
          val diedWaiters = totalWaiters - population/2
          var i = 0
          while (totalWaiters > population/2) {
            //check (totalWaiters, n-i) strisctement décroissante
            println("Town > debug infinite loop 1")
            println("totalWaiters: "+totalWaiters)
            println("population: "+population)
            println("waiters")
            waitersInt.foreach(x => println(x.value))
            println("diedWaiters: "+diedWaiters)
            try {
              if (waiters(i) > 0) {
                println(diedWaiters, i, waiters(i))
                var newDeads = waiters(i).min(diedWaiters/5+1)
                waitersInt(i).set((waiters(i) - newDeads))
                totalWaiters -= newDeads
              }
              i += 1
            } catch {
              case e: IndexOutOfBoundsException => i = 0
            }
          }
          println("waiters died")
        }
      }
    }
    // only 1 town => no waiting passengers
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
    // requests = new ListBuffer[Good]
    // needsInt = new ListBuffer[IntegerProperty]
    // needsStr = new ListBuffer[StringProperty]
    // pricesInt = new ListBuffer[IntegerProperty]
    // for (data <- printData) {
    //   // cancel requests (ugly)
    //   if (data._2.value.length > 5) printData -= data
    // }
    townManager.throwEvent("["+name+"] Everyone is dead here...")
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
         if (stock.updateExpiredProducts(townManager.getTime())) townManager.throwEvent("["+name+"] Be careful! Some of your food is expiring!")
        updateEconomy()
      }
      if (population <= minPopulation) {
        // reinit the whole city
        reInit()
      }
    }
  }

  // def position : GridLocation = pos
  // def name : String = _name.value
  // def name_= (new_name: String) = _name.set(new_name)

  // is it still useful ?
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
    if (i == 0) {
      val food = Product.foods(scala.util.Random.nextInt(Product.foods.size))
      newRequest(food, ((feedPopulation(population*nutritiousNeeds)/(5*food.nutritiousness)).toInt))
    }
    if (i == 1) {
      val purchase = Product.purchases(scala.util.Random.nextInt(Product.purchases.size))
      newRequest(purchase,(population/(purchase.price)).toInt)
    }
  }
  newRandomRequest(1)
  def updateConsumption() = {
    val needs = population*nutritiousNeeds
    if (scala.util.Random.nextInt(50) == 1 && (needs > (dietTime/6))) newRandomRequest(0)
    if (scala.util.Random.nextInt(70) == 1) newRandomRequest(1)
    // if (hunger % lunchTime == 0) {
      val stayingNeeds = feedPopulation(needs)
      hunger = (hunger*stayingNeeds/needs).toInt
      if (hunger > dietTime) {
        // // people ask for the food the less quantity they have
        // var lackingFoodIndex = -1
        // var lackingFoodQuantity = 100
        // for (i <- 0 to stock.productsTypes.length-1) {
        //   stock.productsTypes(i) match {
        //     case f: Food => {
        //       if (stock.product(i) < lackingFoodQuantity) {
        //         lackingFoodIndex = i
        //         lackingFoodQuantity = stock.product(i)
        //       }
        //     }
        //     case _ => ()
        //   }
        // }
        // PROVISOIRE
        // else {
        //   stock.productsTypes(lackingFoodIndex) match {
        //     case f: Food => newRequest(f, (stayingNeeds/f.nutritiousness).toInt)
        //     case _ => println("tycoon > objects > structure > Town : food has been magically transformed into something else")
        //   }
        // }
        if (hunger > starvingTime) {
          population -= hunger*2
          if (!alreadyStarving) {
            townManager.throwEvent("["+name+"] People are starving, nur ein Gott kann sie retten...")
            newRequest(Product.Corn, ((stayingNeeds/(5*Product.Corn.nutritiousness)).toInt))
          }
          alreadyStarving = true
        }
      }
    // }
    hunger += 1
  }

}
