package tycoon.objects.structure

import scala.util.Random
import scala.collection.mutable.ListBuffer

import tycoon.objects.good._
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile

import scalafx.beans.property.{IntegerProperty, StringProperty}

// once built, a mine can produce a limited quantity of iron (iron_amount), the quantity of extracted iron depends on the number of diggers and on the quantity that has already been extracted (it is harder and harder to find iron)


case class Mine(pos: GridLocation, id: Int) extends Facility(pos, id) {
  tile = Tile.mine

  protected val r = scala.util.Random

  val production_time = 100

  // list of available products:
  // for each product, necesary to indicate type, production_per_period and total extractable amount

  // initialization
  var products = new ListBuffer[Good]
  var production_per_period = new ListBuffer[Int]
  var extractable_amount = new ListBuffer[Int]

  def extract(id: Int) : Int = stocks(id).value
  def extract_= (id: Int, new_stock: Int) = stocks(id).set(new_stock)

  // here are added new products
  products += new Good("Coal")
  production_per_period += (10+r.nextInt(10))
  extractable_amount += 1000

  products += new Good("Iron")
  production_per_period += (5+r.nextInt(5))
  extractable_amount+= (50+r.nextInt(100))

  products += new Good("Gold")
  production_per_period += r.nextInt(2)
  extractable_amount += r.nextInt(50)

  displayProducts()

  // update production

  def update_production(i: Int) = {
    stocks(i).set(extract(i) + production_per_period(i))
  }

  override def update(dt: Double) = {
    if (r.nextInt(500) == 0) workers = 0 // rockslide
    intern_time += dt*workers
    if(intern_time > production_time) {
      for (i <- 0 to products.length - 1) update_production(i)
      intern_time -= production_time
    }
  }

  // val product = new Iron()

  //var production_per_period : Int = (diggers * (iron_amount - already_produced))/100




  //val price = game.mine_price //To choose
}
