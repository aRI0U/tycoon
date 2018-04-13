package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import tycoon.objects.good._
import tycoon.objects.structure._

// maybe useless
trait Container {
  val maxSpace : Double
  var remainingSpace : Double
  val merchandises : ListBuffer[Merchandise]
  val mManager : MerchandisesManager

  def embark(structure: Structure, stops: ListBuffer[Structure]) : Unit = {
    stops.foreach(s => mManager.addStop(s))
    structure match {
      case t: Town => includeRequests(t)
      case f: Facility => {
        // determine pertinent products to embark
        var usefulIndices = new ListBuffer[Int]
        for (i <- 0 to mManager.requests.length - 1) {
          for (p <- mManager.requests(i)._2) {
            if (mManager.notVisited(i)) usefulIndices += f.stock.getIndex(p)
          }
        }
        usefulIndices = usefulIndices.filter(_ != -1)
        // embark selected products
        for (i <- usefulIndices) {
          val product = f.stock.productsTypes(i)
          val quantity = f.stock.stocks(i).min((remainingSpace/product.size).toInt)
          f.stock.giveMerchandisesWIndex(i, product, merchandises, quantity, m => (!m.kind.liquid || m.packed))
          remainingSpace -= product.size*quantity
        }
      }
      case a: Airport => a.dependanceTown match {
        case Some(town) => includeRequests(town)
        case None => ()
      }
      case d: Dock => d.dependanceTown match {
        case Some(town) => includeRequests(town)
        case None => ()
      }
    }
  }

  def debark(structure: Structure) = {
    val i = mManager.stops.indexOf(structure)
    mManager.distribute(merchandises, structure)
    // update remainingSpace
    remainingSpace = maxSpace
    merchandises.foreach(m => remainingSpace -= m.quantity*m.kind.size)
    structure match {
      case t: Town => ()
      case _ => ()
    }
    mManager.notVisited(i) = false
  }

  def includeRequests(town: Town) = {
    for (i <- 0 to town.stock.productsTypes.length - 1) {
      if (town.stock.stocks(i) > 2*town.stock.requests(i)) {
        for (r <- mManager.requests) {
          for (good <- r._2) {
            if (good == town.stock.productsTypes(i)) {
              val quantity = town.stock.stocks(i).min((remainingSpace/good.size).toInt)
              town.stock.giveMerchandisesWIndex(i, good, merchandises, quantity, m => (!m.kind.liquid || m.packed))
              remainingSpace -= good.size*quantity
            }
          }
        }
      }
    }
  }
}
