package tycoon.objects.structure

import scala.util.Random
import scala.collection.immutable.List

import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile
import tycoon.objects.good._

case class Factory(pos: GridLocation, id: Int) extends Facility(pos, id) {
  tile = Tile.factory

  val productionTime = 5

  val initialProductsList = List(List(("Coal",3)))

  // disponible products

  products += new Ore("Coal")

  products += new Food("Cake")

  displayProducts()

  def convertedInto(initialProducts: List[(String,Int)]) : List[(String,Int)] = {
    // possible conversions are hardcoded here
    initialProducts match {
      case List(("Coal",3)) => List(("Cake",1))
      case _ => List()
    }
  }


  def process(initialProducts: List[(String,Int)], finalProducts: List[(String,Int)]) = {
    // determine how much could be processed
    var producedQuantity = workers
    for (product <- initialProducts) {
      for (i <- 0 to products.length-1) {
        if (product._1 == products(i).label) {
          var m = stocks(i)/product._2
          if (m < producedQuantity) producedQuantity = m
        }
      }
    }
    //process (not optimized)
    for (i <- 0 to products.length-1) {
      // consumption
      for (product <- initialProducts) {
        if (products(i).label == product._1) {
          stocksInt(i).set(stocks(i) - producedQuantity * product._2)
        }
      }
      // production
      for (product <- finalProducts) {
        if (products(i).label == product._1) {
          stocksInt(i).set(stocks(i) + producedQuantity * product._2)
        }
      }
    }
  }

  def updateProduction() = {
    for (products <- initialProductsList) {
      process(products, convertedInto(products))
    }
  }

  override def update(dt: Double) = {
    intern_time += dt
    if (intern_time > productionTime) {
      updateProduction()
      intern_time -= productionTime
    }
  }

}
