package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._
import tycoon.objects.good._
import tycoon.ui.Renderable
import tycoon.ui.Tile
import tycoon.game.{GridLocation, Player}
import tycoon.objects.railway._


case class GoodsCarriage(_owner: Player) extends Carriage(_owner) {
  tile = Tile.goodsWagonR
  val maxSpace : Int = 10
  var remainingSpace : Int = maxSpace
  var merchandises = new ListBuffer[Merchandise]

  override def embark(structure: Structure, stops: ListBuffer[Structure]) = {
    structure match {
      case t: Town => () // must be modified to include requests from towns onstocks(i)/(product.weight) stops
      case f: Facility => {
        for (i <- 0 to f.products.length - 1) {
          val product = f.products(i)
          // determine how much quantity can be transported in the carriage
          var quantity = remainingSpace.min(f.stocks(i)/(product.weight))
          println("GoodsCarriage > just embarked" + quantity)
          // remove the product from the facility
          f.stocksInt(i).set(f.stocks(i) - quantity)
          // add the product in the carriage
          merchandises += new Merchandise(product, quantity)
          remainingSpace -= quantity*product.weight
        }
      }
    }
  }

  override def debark(structure: Structure) = {
    structure match {
      case t: Town => ()
      case f: Facility => {
        for (i <- 0 to f.products.length-1) {
          for (merch <- merchandises) {
            if (merch.kind.label == f.products(i).label) {
              // add the product
              f.stocksInt(i).set(f.stocks(i) + merch.quantity)
              // empty the carriage
              merchandises -= merch
            }
          }
        }
      }
    }
  }
}
