package tycoon.objects.structure

import scala.util.Random
import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

import tycoon.game._
import tycoon.ui.Tile
import tycoon.objects.good._

case class Factory(pos: GridLocation, id: Int, tManager: TownManager) extends Facility(pos, id, tManager) {
  tile = Tile.factory

  val productionTime = 5

  protected val _recipesList : ListBuffer[List[(Good, Int)]] = ListBuffer(List((Product.Coal,3)))

  def recipesList = _recipesList

  // disponible products

  def addRecipeProducts(recipe: List[(Good, Int)]) = {
    for (p <- recipe) {
      stock.newProduct(p._1, 0)
      // var i = 0
      // while (i < products.length && !areSameGoods(products(i), p._1)) i += 1
      // if (i == products.length) {
      //   products += p._1
      //   datedProducts += new ListBuffer[Merchandise]
      // }
    }
  }

  def initProducts() = {
    recipesList.foreach(r => addRecipeProducts(r))

    for (i <- 0 to recipesList.length-1)    addRecipeProducts(convertedInto(i))

    displayProducts()
  }

  initProducts()

  def convertedInto(recipeId: Int) : List[(Good,Int)] = {
    // possible conversions are hardcoded here
    recipeId match {
      case 0 => List((Product.Cake,1))
      case _ => throw new IndexOutOfBoundsException
    }
  }

  def process(initialProducts: List[(Good,Int)], finalProducts: List[(Good,Int)]) = {
    // determine the indices of the useful products
    var usefulIndices = new ListBuffer[Int]
    for (product <- initialProducts) {
      usefulIndices += stock.getIndex(product._1)
      // for (i <- 0 to stock.productsTypes.length-1) {
      //   if (product._1 == stock.productsTypes(i)) usefulIndices += i
      // }
    }
    usefulIndices = usefulIndices.filter(_ != -1)

    // determine how much could be processed
    var producedQuantity = workers
    for (j <- 0 to usefulIndices.length-1) {
      val i = usefulIndices(j)
      val m = stock.stocks(i)/initialProducts(j)._2
      if (m < producedQuantity) producedQuantity = m
    }
    // consume the initial products
    for (j <- 0 to usefulIndices.length-1) {
      val i = usefulIndices(j)
      var consumedQuantity = producedQuantity * initialProducts(j)._2
      var trash = new ListBuffer[Merchandise]
      stock.giveMerchandisesWIndex(i, stock.productsTypes(i), trash, consumedQuantity)
      // while (consumedQuantity > 0 && datedProducts(i).length > 0) {
      //   val m = datedProducts(i)(0)
      //   datedProducts(i) -= m
      //   // eventually "cut" the merchandises
      //   if (m.quantity > consumedQuantity) {
      //     datedProducts(i) += new Merchandise(m.kind, m.quantity - consumedQuantity, m.productionDate)
      //   }
      //   consumedQuantity -= m.quantity
      // }
    }
    // create the final products

    for (p <- finalProducts) {
      stock.getMerchandise(new Merchandise(p._1, producedQuantity*p._2, townManager.getTime()))
    }
    // for (i <- 0 to products.length-1) {
    //   for (product <- finalProducts) {
    //     if (areSameGoods(products(i), product._1)) {
    //       datedProducts(i) += new Merchandise(products(i), producedQuantity*product._2, townManager.getTime())
    //     }
    //   }
    // }
  }

  def updateProduction() = {
    for (i <- 0 to recipesList.length-1) {
      process(recipesList(i), convertedInto(i))
    }
  }

  override def update(dt: Double) = {
    if (workers > 0) {
      if (r.nextInt((7000-workers).max(100)) == 0) {
        val deads = r.nextInt(workers.min(10))
        workers -= deads
        townManager.throwEvent("[Factory n°"+id+"] Industrial accident: "+deads+" workers tragically passed away...")
      }
      internTime += dt
      if (internTime > productionTime) {
        updateProduction()
        stock.updateExpiredProducts(townManager.getTime())
        internTime -= productionTime
      }
    }
  }
}
