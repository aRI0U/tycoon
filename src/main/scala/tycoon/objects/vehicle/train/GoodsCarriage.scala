package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._
import tycoon.objects.good._
import tycoon.ui.Renderable
import tycoon.ui.Tile
import tycoon.game.{GridLocation, Player}
import tycoon.objects.railway._

import scalafx.beans.property.IntegerProperty


case class GoodsCarriage(id: Int, initialTown: Structure, _owner: Player) extends Carriage(id, initialTown, _owner) {
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
        case t: Town => {
          for (good <- t.requests) requests += good.label
        }
        case _ => ()
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
      case t: Town => {
        var i = 0 // number of satisfied requests
        for (merch <- merchandises) {
          var i = 0
          while (i < t.requests.length) {
            if (merch.kind.label == t.requests(i).label) {
              val soldQuantity = merch.quantity.min(t.needs(i))
              // add the product to the town
              var index = t.products.indexOf(merch.kind)
              if (index == -1) {
                t.products += merch.kind
                t.stocksInt += IntegerProperty(soldQuantity)
              }
              else {
                t.stocksInt(index).set(t.stocks(index) + soldQuantity)
              }
              // delete the merchandise from the carriage
              if (merch.quantity > soldQuantity) {
                merch.quantity -= soldQuantity
              }
              else merchandises -= merch
              remainingSpace += soldQuantity*merch.kind.size
              weight -= soldQuantity*merch.kind.weight
              // be paid
              owner.earn(prices(i)*soldQuantity)
              // satisfy the request
              if (!t.satisfyRequest(merch.kind, i, soldQuantity)) i += 1
            }
            else i += 1
          }
        }
      }
      case f: Facility => {
        for (i <- 0 to f.products.length-1) {
          for (merch <- merchandises) {
            if (merch.kind.label == f.products(i).label) {
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


object GoodsCarriage {
  val Price: Int = 30
}
