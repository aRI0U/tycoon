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

  val productionTime = 100
  setName("Mine " + id.toString)

  // list of available products:
  // for each product, necesary to indicate type, productionPerPeriod and total extractable amount

  // initialization
  var productionPerPeriod = new ListBuffer[Int]
  var extractableAmount = new ListBuffer[Int]
  var allExtracted = new ListBuffer[Boolean]

  // here are added new products
  stock.newProduct(Product.Coal, 0)
  productionPerPeriod += (10+r.nextInt(10))
  extractableAmount += 50000
  allExtracted += false

  stock.newProduct(Product.Iron, 0)
  productionPerPeriod += (5+r.nextInt(5))
  extractableAmount+= (500+r.nextInt(1000))
  allExtracted += false

  stock.newProduct(Product.Gold, 0)
  productionPerPeriod += r.nextInt(3)
  extractableAmount += r.nextInt(100)
  allExtracted += false

  // update production

  def updateProduction(i: Int) = {
    if (!allExtracted(i)) {
      stock.getMerchandiseWIndex(new Merchandise(stock.productsTypes(i), productionPerPeriod(i), townManager.getTime()), i)
      // datedProducts(i) += new Merchandise(products(i), productionPerPeriod(i), townManager.getTime())
      // stocksInt(i).set(stocks(i) + productionPerPeriod(i))
      if (stock.stocks(i) >= extractableAmount(i)) {
        allExtracted(i) = true
        townManager.throwEvent("[Mine n°"+id+"] "+stock.productsTypes(i).label+"s: everything has been extracted in this mine")
      }
    }
  }

  override def update(dt: Double) = {
    if (workers > 0) {
      // random rockslides can kill workers
      if (r.nextInt(10000) == 0) {
        workers = 0
        townManager.throwEvent("[Mine n°"+id+"] Rockslide: All diggers died!")
      }
      internTime += dt*workers
      if(internTime > productionTime) {
        for (i <- 0 to stock.productsTypes.length - 1) updateProduction(i)
        internTime -= productionTime
      }
    }
  }
  //val price = game.mine_price //To choose
}
