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
  val maxSpace : Double = 100
  var remainingSpace : Double = maxSpace
  var merchandises = new ListBuffer[Merchandise]


  def determineRequests(stops: ListBuffer[Structure]) : ListBuffer[String] = {
    var requests = new ListBuffer[String]
    for (structure <- stops) {
      structure match {
        case f: Factory => {
          for (l <- f.initialProductsList) {
            for (p <- l) requests += p._1
          }
        }
        case _  => ()
      }
    }
    requests
  }

  override def embark(structure: Structure, stops: ListBuffer[Structure]) = {
    println("GoodsCarriage > embark")
    val requests = determineRequests(stops)
    structure match {
      case t: Town => () // must be modified to include requests from towns on stops
      case f: Facility => {
        for (i <- 0 to f.products.length - 1) {
          val product = f.products(i)
          if (requests.contains(product.label)) {
            println(product, remainingSpace, f.stocks(i))
            // determine how much quantity can be transported in the carriage
            var quantity = (remainingSpace.toInt).min((f.stocks(i)/(product.size)).toInt)
            if (quantity > 0) {
              // remove the product from the facility
              f.stocksInt(i).set(f.stocks(i) - quantity)
              // add the product in the carriage
              merchandises += new Merchandise(product, quantity)
              println("GoodsCarriage > just embarked" + quantity)
              remainingSpace -= quantity*product.size
              weight += quantity*product.weight
              println(product, remainingSpace, f.stocks(i))
            }
          }
        }
      }
    }
  }

  override def debark(structure: Structure) = {
    println("GoodsCarriage > debark")
    structure match {
      case t: Town => ()
      case f: Facility => {
        for (i <- 0 to f.products.length-1) {
          for (merch <- merchandises) {
            if (merch.kind.label == f.products(i).label) {
              println(merch.kind, remainingSpace, f.stocks(i))
              println(merch)
              // add the product
              f.stocksInt(i).set(f.stocks(i) + merch.quantity)
              // empty the carriage
              merchandises -= merch
              remainingSpace += merch.quantity*merch.kind.size
              weight -= merch.quantity*merch.kind.weight
              println(merch.kind, remainingSpace, f.stocks(i))
            }
          }
        }
      }
    }
  }
}
