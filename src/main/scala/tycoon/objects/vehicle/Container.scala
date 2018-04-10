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
    // println("merchandises: "+merchandises)
    // println("GoodsCarriage > embark from "+structure)
    // println("stops: "+stops)
    stops.foreach(s => mManager.addStop(s))
    //println("planning:"+ mManager.flattenedRequests)
    structure match {
      case t: Town => includeRequests(t) // must be modified to include requests from towns on stops
      case f: Facility => {
        // determine pertinent products to embark
        var usefulIndices = new ListBuffer[Int]
        for (i <- 0 to mManager.requests.length - 1) {
          for (p <- mManager.requests(i)._2) {
            if (mManager.notVisited(i)) usefulIndices += f.stock.getIndex(p)
          }
        }
        usefulIndices = usefulIndices.filter(_ != -1)
        //println("usefulIndices: "+usefulIndices)

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
    }
    // println("merchandises: "+merchandises)
    // println("quit structure"+structure)
  }

  def debark(structure: Structure) = {
    // println("merchandises: "+merchandises)
    // println("GoodsCarriage > debark")
    val i = mManager.stops.indexOf(structure)    // for (m <- mManager.distribution(i)) structure.stock.getMerchandise(m)
    // mManager.removeStop(structure)
    mManager.distribute(merchandises, structure)
    // update remainingSpace
    remainingSpace = maxSpace
    merchandises.foreach(m => remainingSpace -= m.quantity*m.kind.size)
    // for (m <- mManager.distribution(i)) structure.stock.getMerchandise(m)
    // mManager.removeStop(structure)
    structure match {
      case t: Town => ()
      case _ => ()
    }
    mManager.notVisited(i) = false
  }

  def includeRequests(town: Town) = {
    // test
    merchandises += new Merchandise(Product.Cake, 100, 0)
  }
}
