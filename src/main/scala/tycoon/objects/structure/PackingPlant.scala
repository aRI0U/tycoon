package tycoon.objects.structure

import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

import tycoon.game._
import tycoon.objects.good._

class PackingPlant(pos: GridLocation, id: Int, tManager: TownManager) extends Factory(pos, id, tManager) {

  override val recipesList = new ListBuffer[List[(Good,Int)]]

  override def convertedInto(recipeId: Int) : List[(Good,Int)] = List(recipesList(recipeId)(0))

  override def process(initialProducts: List[(Good,Int)], finalProducts: List[(Good,Int)]) = {
    var toPackIndex = 0
    var packagingIndex = 0
    // if these two loops create an error, the problem comes from GoodsCarriage > debark
    while (toPackIndex < products.length && !areSameGoods(initialProducts(0)._1, products(toPackIndex))) toPackIndex += 1
    while (packagingIndex < products.length && !areSameGoods(initialProducts(0)._1, products(packagingIndex))) packagingIndex += 1
    val packableQuantity = stocks(toPackIndex).min(stocks(packagingIndex))
    // consume packaging
    var notConsumed = packableQuantity
    while (notConsumed > 0) {
      val merch = datedProducts(packagingIndex)(0)
      if (merch.quantity > notConsumed) {
        merch.quantity -= notConsumed
        notConsumed = 0
      }
      else {
        notConsumed -= merch.quantity
        datedProducts(packagingIndex).remove(0)
      }
    }
    stocksInt(packagingIndex).set(stocks(packagingIndex) - packableQuantity)
    // pack
    var notPacked = packableQuantity
    var i = 0
    while (notPacked > 0) {
      val lastPacked = datedProducts(toPackIndex)(i)
      lastPacked.packed = true
      if (lastPacked.quantity > notPacked) {
        datedProducts(toPackIndex) += new Merchandise(lastPacked.kind, lastPacked.quantity - notPacked, lastPacked.productionDate)
        lastPacked.quantity = notPacked
        notPacked = 0
      }
      else notPacked -= lastPacked.quantity
    }
  }
}
