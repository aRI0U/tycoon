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

  val recipesList : ListBuffer[List[(Good, Int)]] = ListBuffer(List((new Ore("Coal"),3)))

  // disponible products

  def addRecipeProducts(recipe: List[(Good, Int)]) = {
    for (p <- recipe) {
      var i = 0
      while (i < products.length && !areSameGoods(products(i), p._1)) i += 1
      if (i == products.length) {
        products += p._1
        datedProducts += new ListBuffer[Merchandise]
      }
    }
  }

  recipesList.foreach(r => addRecipeProducts(r))

  for (i <- 0 to recipesList.length-1)    addRecipeProducts(convertedInto(i))

  displayProducts()

  def convertedInto(recipeId: Int) : List[(Good,Int)] = {
    // possible conversions are hardcoded here
    recipeId match {
      case 0 => List((new Food("Cake"),1))
      case _ => throw new IndexOutOfBoundsException
    }
  }

  def process(initialProducts: List[(Good,Int)], finalProducts: List[(Good,Int)]) = {
    // determine the indices of the useful products
    var usefulIndices = new ListBuffer[Int]
    for (product <- initialProducts) {
      for (i <- 0 to products.length-1) {
        if (areSameGoods(product._1, products(i))) usefulIndices += i
      }
    }
    // determine how much could be processed
    var producedQuantity = workers
    for (j <- 0 to usefulIndices.length-1) {
      val i = usefulIndices(j)
      if (areSameGoods(initialProducts(j)._1, products(i))) {
        val m = stocks(i)/initialProducts(j)._2
        if (m < producedQuantity) producedQuantity = m
      }
    }
    // consume the initial products
    for (j <- 0 to usefulIndices.length-1) {
      val i = usefulIndices(j)
      var consumedQuantity = producedQuantity * initialProducts(j)._2
      while (consumedQuantity > 0 && datedProducts(i).length > 0) {
        val m = datedProducts(i)(0)
        datedProducts(i) -= m
        // eventually "cut" the merchandises
        if (m.quantity > consumedQuantity) {
          datedProducts(i) += new Merchandise(m.kind, m.quantity - consumedQuantity, m.productionDate)
        }
        consumedQuantity -= m.quantity
      }
    }
    // create the final products
    for (i <- 0 to products.length-1) {
      for (product <- finalProducts) {
        if (areSameGoods(products(i), product._1)) {
          datedProducts(i) += new Merchandise(products(i), producedQuantity*product._2, townManager.getTime())
        }
      }
    }
  }

  def updateProduction() = {
    for (i <- 0 to recipesList.length-1) {
      process(recipesList(i), convertedInto(i))
    }
    updateStocks()
  }

  override def update(dt: Double) = {
    if (workers > 0) {
      if (r.nextInt((7000-workers).max(100)) == 0) {
        val deads = r.nextInt(10)
        workers -= deads
        throwEvent("[Factory n°"+id+"] Industrial accident: "+deads+" workers tragically passed away...")
      }
      intern_time += dt
      if (intern_time > productionTime) {
        updateProduction()
        updateExpiredProducts(townManager.getTime())
        intern_time -= productionTime
      }
    }
  }
}
