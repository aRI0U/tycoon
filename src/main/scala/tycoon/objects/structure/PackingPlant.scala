package tycoon.objects.structure

import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

import tycoon.game._
import tycoon.objects.good._

class PackingPlant(pos: GridLocation, id: Int, townManager: TownManager, owner: Player) extends Factory(pos, id, townManager, owner) {
  override val recipesList = new ListBuffer[List[(Good,Int)]]
  setName("Packing Plant " + id.toString)

  override protected val _recipesList : ListBuffer[List[(Good, Int)]] = new ListBuffer[List[(Good,Int)]]

  override def initProducts() = { }

  override def convertedInto(recipeId: Int) : List[(Good,Int)] = List(recipesList(recipeId)(0))

  override def process(initialProducts: List[(Good,Int)], finalProducts: List[(Good,Int)]) = {
    var toPackIndex = stock.getIndex(initialProducts(0)._1)
    var packagingIndex = stock.getIndex(initialProducts(1)._1)

    val packableQuantity = stock.stocks(toPackIndex).min(stock.stocks(packagingIndex))
    // consume packaging
    var trash = new ListBuffer[Merchandise]
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
}
