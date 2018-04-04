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

  val production_time = 100

  // list of available products:
  // for each product, necesary to indicate type, productionPerPeriod and total extractable amount

  // initialization
  var productionPerPeriod = new ListBuffer[Int]
  var extractable_amount = new ListBuffer[Int]

  // here are added new products
  products += new Ore("Coal")
  productionPerPeriod += (10+r.nextInt(10))
  extractable_amount += 1000

  products += new Ore("Iron")
  productionPerPeriod += (5+r.nextInt(5))
  extractable_amount+= (50+r.nextInt(100))

  products += new Ore("Gold")
  productionPerPeriod += r.nextInt(2)
  extractable_amount += r.nextInt(50)

  displayProducts()

  // update production

  def update_production(i: Int) = {
    if (stocks(i) < extractable_amount(i))    stocksInt(i).set(stocks(i) + productionPerPeriod(i))
  }

  override def update(dt: Double) = {
    // random rockslides can kill workers
    if (workers > 0 && r.nextInt(10000) == 0) {
      workers = 0
      throwEvent("[Mine n°"+id+"] Rockslide: All diggers died!")
    }
    intern_time += dt*workers
    if(intern_time > production_time) {
      for (i <- 0 to products.length - 1) update_production(i)
      intern_time -= production_time
    }
  }
  //val price = game.mine_price //To choose
}
