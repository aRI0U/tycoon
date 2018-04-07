package tycoon.objects.structure

import scala.util.Random
import scala.collection.mutable.ListBuffer

import tycoon.objects.good._
import tycoon.game._
import tycoon.ui.Tile

import scalafx.beans.property.{IntegerProperty, StringProperty}

// once built, a mine can produce a limited quantity of iron (iron_amount), the quantity of extracted iron depends on the number of diggers and on the quantity that has already been extracted (it is harder and harder to find iron)


case class Mine(pos: GridLocation, id: Int, tManager: TownManager) extends Facility(pos, id, tManager) {
  tile = Tile.mine

  val production_time = 100

  // list of available products:
  // for each product, necesary to indicate type, productionPerPeriod and total extractable amount

  // initialization
  var productionPerPeriod = new ListBuffer[Int]
  var extractableAmount = new ListBuffer[Int]
  var allExtracted = new ListBuffer[Boolean]

  // here are added new products
  products += new Ore("Coal")
  datedProducts += new ListBuffer[Merchandise]
  productionPerPeriod += (10+r.nextInt(10))
  extractableAmount += 50000
  allExtracted += false

  products += new Ore("Iron")
  datedProducts += new ListBuffer[Merchandise]
  productionPerPeriod += (5+r.nextInt(5))
  extractableAmount+= (500+r.nextInt(1000))
  allExtracted += false

  products += new Ore("Gold")
  datedProducts += new ListBuffer[Merchandise]
  productionPerPeriod += r.nextInt(2)
  extractableAmount += r.nextInt(100)
  allExtracted += false

  displayProducts()

  // update production

  def update_production(i: Int) = {
    if (!allExtracted(i)) {
      datedProducts(i) += new Merchandise(products(i), productionPerPeriod(i), townManager.getTime())
      stocksInt(i).set(stocks(i) + productionPerPeriod(i))
      if (stocks(i) >= extractableAmount(i)) {
        allExtracted(i) = true
        throwEvent("[Mine n°"+id+"] "+products(i).label+"s: everything has been extracted in this mine")
      }
    }
  }

  override def update(dt: Double) = {
    if (workers > 0) {
      // random rockslides can kill workers
      if (r.nextInt(10000) == 0) {
        workers = 0
        throwEvent("[Mine n°"+id+"] Rockslide: All diggers died!")
      }
      intern_time += dt*workers
      if(intern_time > production_time) {
        for (i <- 0 to products.length - 1) update_production(i)
        intern_time -= production_time
      }
    }
  }
  //val price = game.mine_price //To choose
}
