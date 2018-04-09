package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.game.Player
import tycoon.objects.structure._

class TankCar(id: Int, initialTown: Structure, _owner: Player) extends GoodsCarriage(id, initialTown, _owner) {

  override def embark(structure: Structure, stops: ListBuffer[Structure]) = {
    println("merchandises: "+merchandises)
    println("GoodsCarriage > embark from "+structure)
    println("stops: "+stops)
    stops.foreach(s => planning.addStop(s))
    //println("planning:"+ planning.flattenedRequests)
    structure match {
      case t: Town => () // must be modified to include requests from towns on stops
      case f: Facility => {
        // determine pertinent products to embark
        var usefulIndices = new ListBuffer[Int]
        for (i <- 0 to planning.requests.length - 1) {
          for (p <- planning.requests(i)._2) {
            if (planning.notVisited(i)) usefulIndices += f.stock.getIndex(p)
          }
        }
        usefulIndices = usefulIndices.filter(_ != -1)
        println("usefulIndices: "+usefulIndices)

        // embark selected products
        for (i <- usefulIndices) {
          val product = f.stock.productsTypes(i)
          val quantity = f.stock.stocks(i).min((remainingSpace/product.size).toInt)
          f.stock.giveMerchandisesWIndex(i, product, merchandises, quantity, m => (m.kind.liquid && !m.packed))
          remainingSpace -= product.size*quantity
        }
      }
    }
    println("merchandises: "+merchandises)
    println("quit structure"+structure)
  }
}
