package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import tycoon.objects.good._
import tycoon.objects.structure._

trait LiquidContainer extends Container {

  override def embark(structure: Structure, stops: ListBuffer[Structure]) = {
    println("- tank car -")
    println("merchandises: "+merchandises)
    println("GoodsCarriage > embark from "+structure)
    println("stops: "+stops)
    stops.foreach(s => mManager.addStop(s))
    //println("planning:"+ mManager.flattenedRequests)
    structure match {
      case t: Town => () // must be modified to include requests from towns on stops
      case f: Facility => {
        // determine pertinent products to embark
        var usefulIndices = new ListBuffer[Int]
        for (i <- 0 to mManager.requests.length - 1) {
          for (p <- mManager.requests(i)._2) {
            if (p.liquid && mManager.notVisited(i)) usefulIndices += f.stock.getIndex(p)
          }
        }
        usefulIndices = usefulIndices.filter(_ != -1)
        println("usefulIndices: "+usefulIndices)

        // only one kind of product can be transported by a tank car
        var i = 0
        var flag = true // flag indicates if no product has been embarked yet
        while (flag && i < usefulIndices.length) {
          val j = usefulIndices(i)
          val product = f.stock.productsTypes(j)
          println("product:"+product)
          val prevRemainingSpace = remainingSpace
          val quantity = f.stock.stocks(j).min((remainingSpace/product.size).toInt)
          f.stock.giveMerchandisesWIndex(j, product, merchandises, quantity, m => (m.kind.liquid && !m.packed))
          remainingSpace -= product.size*quantity
          i+=1
          flag = (remainingSpace == prevRemainingSpace) // flag stays true iff no product has been added
        }
      }
    }
    println("merchandises: "+merchandises)
    println("quit structure"+structure)
  }
}
