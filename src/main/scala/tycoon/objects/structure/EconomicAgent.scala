package tycoon.objects.structure

import scala.collection.mutable.ListBuffer

import tycoon.game._
import tycoon.objects.good._

import scalafx.beans.property.{DoubleProperty, IntegerProperty}

abstract class EconomicAgent(pos: GridLocation, id: Int, townManager: TownManager) extends Structure(pos, id) {
   val stock = new Stock(this)
   val weightings = new ListBuffer[Weighting]
   val multipliers = new ListBuffer[(Good, DoubleProperty)]
//
   def report(good: Good, stocks: IntegerProperty, requests: IntegerProperty) = good.newEmergence(this, stocks, requests)
//
//   def getGoodData(good: EconomicGood) : (EconomicGood, DoubleProperty) = {
//     var i = 0
//     while (i < goods.length && goods(i)._1.kind != good.kind) i += 1
//     if (i == goods.length) {
//       val goodData = new Tuple2(good, DoubleProperty(1))
//       computeMultiplier(goodData)
//       goods += goodData
//     }
//     goods(i)
//   }
//

  def getMultiplier(good: Good) : DoubleProperty = {
    var i = 0
    while (i < multipliers.length && multipliers(i)._1 != good) i += 1
    if (i == multipliers.length) multipliers += new Tuple2(good, DoubleProperty(1.0))
    multipliers(i)._2
  }

  def computeMultiplier(good: Good, multiplier: DoubleProperty) = {
    var totalStocks = 0.0
    var totalRequests = 0.0
    for (p <- good.totalProducts) {
      val weighting = getWeighting(p._1).coeff
      totalStocks += p._2.value*weighting
      totalRequests += p._3.value*weighting
      //println("stocks ="+p._2.value)
      //println("requests ="+p._3.value)
    }
    var newMultiplier = 0.0
    if (totalStocks > 0) newMultiplier = totalRequests/totalStocks
    else newMultiplier = 1.0
    newMultiplier = (50.0*(multiplier.value+newMultiplier) + 0.5).toInt.toDouble/100
    //newMultiplier = (newMultiplier * 100 + 0.5).toInt.toDouble/100
    multiplier.set(newMultiplier)
  }

  def updateEconomy() = {
    for (m <- multipliers) computeMultiplier(m._1, m._2)
  }

  def updateWeightings() = {
    weightings.foreach(w => w.updateWeighting(this, townManager))
    normalize
  }

  def newWeighting(s: EconomicAgent) = {
    val w = new Weighting(s, 0.0)
    w.updateWeighting(this, townManager)
    weightings += w
    normalize()
  }

  def getWeighting(s: EconomicAgent) : Weighting = {
    var i = 0
    while (i < weightings.length && weightings(i).structure != s) i += 1
    if (i == weightings.length) {
      newWeighting(s)
    }
    weightings(i)
  }

  def normalize() = {
    var sum = 0.0
    weightings.foreach(w => sum += w.coeff)
    weightings.foreach(w => println(w.structure + " is weighted " + w.coeff))
    println("sum = "+sum)
    if (sum > 0) weightings.foreach(w => w.coeff /= sum)
    weightings.foreach(w => println(w.structure + " is weighted " + w.coeff))
  }
//



//   def updateWeightings() = {
//     for (w <- weightings) {
//       if (w.structure == this) w.coeff = 1.0
//       else {
//         townManager.determineRailwayDistance(this, w.structure) match {
//           case Some(i) => w.coeff = 1/i
//           case None => {
//             val d = townManager.determineEuclidianDistance(this, w.structure)
//             w.coeff = 1/(Math.pow(d,2))
//           }
//         }
//       }
//     }
//     normalize(weightings)
//   }
//
//   def normalize(weightings: ListBuffer[Weighting]) = {
//     var sum = 0.0
//     weightings.foreach(w => sum += w.coeff)
//     weightings.foreach(w => w.coeff /= sum)
//   }
//
//   def newEconomicGood(good: EconomicGood) = {
//     val i = getGoodData(good)
//   }
//
//   def getMultiplier(good: Good) = getGoodData(townManager.getEconomicGood(good))._2
//
//   def updateEconomy() = {
//     val e = townManager.getEconomicGood(Product.Milk)
//     goods.foreach(computeMultiplier)
//     updateWeightings()
//     for (g <- goods) {
//       println(g._1.kind)
//       println("stocks = "+g._1.totalProducts(0)._2.value)
//       println("requests = "+g._1.totalProducts(0)._3.value)
//     }
//   }
}
//
// // here -> stocks/requests from all cities
// class EconomicGood(val kind: Good) {
//   var totalProducts = new ListBuffer[(Structure, IntegerProperty, IntegerProperty)]
//
//   def newEmergence(structure: Structure, stocks: IntegerProperty, requests: IntegerProperty) = {
//     var i = 0
//     while (i < totalProducts.length && totalProducts(i)._1 != structure) i += 1
//     if (i == totalProducts.length) {
//       val stockSaved = new IntegerProperty
//       stockSaved <== stocks
//       val requestSaved = new IntegerProperty
//       requestSaved <== requests
//       totalProducts += new Tuple3(structure, stockSaved, requestSaved)
//     }
//   }
// }
//
class Weighting(val structure: EconomicAgent, d: Double) {
  var coeff = d

  def updateWeighting(s: EconomicAgent, townManager: TownManager) = {
      if (structure == s) coeff = 1.0
      else townManager.determineRailwayDistance(structure, s) match {
        case Some(d) => coeff = 1.0/d
        case None => coeff = 0.0
      }
    }
}
