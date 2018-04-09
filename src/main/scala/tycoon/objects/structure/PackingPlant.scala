package tycoon.objects.structure

import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

import tycoon.game._
import tycoon.objects.good._

class PackingPlant(pos: GridLocation, id: Int, tManager: TownManager) extends Factory(pos, id, tManager) {

  override protected val _recipesList : ListBuffer[List[(Good, Int)]] = new ListBuffer[List[(Good,Int)]]

  override def initProducts() = { }

  override def convertedInto(recipeId: Int) : List[(Good,Int)] = List(recipesList(recipeId)(0))

  override def process(initialProducts: List[(Good,Int)], finalProducts: List[(Good,Int)]) = {
    var toPackIndex = stock.getIndex(initialProducts(0)._1)
    var packagingIndex = stock.getIndex(initialProducts(1)._1)
    // var toPackIndex = 0
    // var packagingIndex = 0
    // // if these two loops create an error, the problem comes from GoodsCarriage > debark
    // while (toPackIndex < products.length && initialProducts(0)._1 == products(toPackIndex)) toPackIndex += 1
    // while (packagingIndex < products.length && !areSameGoods(initialProducts(0)._1, products(packagingIndex))) packagingIndex += 1
    val packableQuantity = stock.stocks(toPackIndex).min(stock.stocks(packagingIndex))
    // consume packaging
    var trash = new ListBuffer[Merchandise]
    //stock.giveMerchandisesWindex(packagingIndex, initialProducts(1)._1, trash, packableQuantity)

    // var notConsumed = packableQuantity
    // while (notConsumed > 0) {
    //   val merch = datedProducts(packagingIndex)(0)
    //   if (merch.quantity > notConsumed) {
    //     merch.quantity -= notConsumed
    //     notConsumed = 0
    //   }
    //   else {
    //     notConsumed -= merch.quantity
    //     datedProducts(packagingIndex).remove(0)
    //   }
    // }
    // stocksInt(packagingIndex).set(stocks(packagingIndex) - packableQuantity)
    // pack
    var notPacked = packableQuantity
    var i = 0
    while (notPacked > 0) {
      val lastPacked = stock.datedProducts(toPackIndex)(i)
      lastPacked.packed = true
      if (lastPacked.quantity > notPacked) {
        stock.datedProducts(toPackIndex) += new Merchandise(lastPacked.kind, lastPacked.quantity - notPacked, lastPacked.productionDate)
        lastPacked.quantity = notPacked
        notPacked = 0
      }
      else notPacked -= lastPacked.quantity
    }
  }

  def updateProducts() = {
    
  }
}
