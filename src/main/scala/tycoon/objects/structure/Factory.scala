package tycoon.objects.structure

import scala.util.Random
import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

import tycoon.game._
import tycoon.ui.Tile
import tycoon.objects.good._

case class Factory(pos: GridLocation, id: Int, tManager: TownManager) extends Facility(pos, id, tManager) {
  tile = Tile.factory

  val productionTime = 5

  val initialProductsList = List(List(("Coal",3)))

  // disponible products

  products += new Ore("Coal")
  datedProducts += new ListBuffer[Merchandise]

  products += new Food("Cake")
  datedProducts += new ListBuffer[Merchandise]

  displayProducts()

  def convertedInto(initialProducts: List[(String,Int)]) : List[(String,Int)] = {
    // possible conversions are hardcoded here
    initialProducts match {
      case List(("Coal",3)) => List(("Cake",1))
      case _ => List()
    }
  }


  def process(initialProducts: List[(String,Int)], finalProducts: List[(String,Int)]) = {
    // determine the indices of the useful products
    var usefulIndices = new ListBuffer[Int]
    for (product <- initialProducts) {
      for (i <- 0 to products.length-1) {
        if (product._1 == products(i).label) usefulIndices += i
      }
    }
    // determine how much could be processed
    var producedQuantity = workers
    for (j <- 0 to usefulIndices.length-1) {
      val i = usefulIndices(j)
      if (initialProducts(j)._1 == products(i).label) {
        val m = stocks(i)/initialProducts(j)._2
        if (m < producedQuantity) producedQuantity = m
      }
    }
    // for (product <- initialProducts) {
    //   for (i <- 0 to products.length-1) {
    //     if (product._1 == products(i).label) {
    //       var m = stocks(i)/product._2
    //       if (m < producedQuantity) producedQuantity = m
    //     }
    //   }

    // consume the initial products
    for (j <- 0 to usefulIndices.length-1) {
      val i = usefulIndices(j)
      var consumedQuantity = producedQuantity * initialProducts(j)._2
      while (consumedQuantity > 0 && datedProducts(i).length > 0) {
        val m = datedProducts(i)(0)
        datedProducts(i) -= m
        // eventually "cut" the merchandises
        if (m.quantity > consumedQuantity) {
          datedProducts(i) += new Merchandise(m.kind, m.quantity - consumedQuantity, m.productionDate)
        }
        consumedQuantity -= m.quantity
      }
    }
    // create the final products
    for (i <- 0 to products.length-1) {
      for (product <- finalProducts) {
        if (products(i).label == product._1) {
          datedProducts(i) += new Merchandise(products(i), producedQuantity*product._2, townManager.getTime())
        }
      }
    }
  }

  def updateProduction() = {
    for (products <- initialProductsList) {
      process(products, convertedInto(products))
    }
    updateStocks()
  }

  override def update(dt: Double) = {
    if (workers > 0) {
      if (r.nextInt((3000-workers).max(0)) == 0) {
        workers -= 1
        throwEvent("[Factory n°"+id+"] Industrial accident: A worker tragically passed away...")
      }
      intern_time += dt
      if (intern_time > productionTime) {
        updateProduction()
        updateExpiredProducts(townManager.getTime())
        intern_time -= productionTime
      }
    }
  }
}
