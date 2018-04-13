package tycoon.objects.structure

import scala.collection.mutable.ListBuffer

import tycoon.game._
import tycoon.objects.good._

import scalafx.beans.property.{DoubleProperty, IntegerProperty}

abstract class EconomicAgent(pos: GridLocation, id: Int, townManager: TownManager) extends Structure(pos, id) {
  val stock = new Stock(this)
  var weightings = new ListBuffer[Weighting]
  var goods = new ListBuffer[(EconomicGood, DoubleProperty)]

  def report(good: Good, stocks: IntegerProperty, requests: IntegerProperty) = townManager.getReport(this, good, stocks, requests)

  def getGoodData(good: EconomicGood) : (EconomicGood, DoubleProperty) = {
    var i = 0
    while (i < goods.length && goods(i)._1.kind != good.kind) i += 1
    if (i == goods.length) {
      val goodData = new Tuple2(good, DoubleProperty(1))
      computeMultiplier(goodData)
      goods += goodData
    }
    goods(i)
  }

  def getWeighting(s: Structure) : Weighting = {
    var i = 0
    while (i < weightings.length && weightings(i).structure != s) i += 1
    if (i == weightings.length) {
      weightings += new Weighting(s,0)
      updateWeightings()
    }
    weightings(i)
  }

  def computeMultiplier(goodData: (EconomicGood, DoubleProperty)) = {
    val prevMultiplier = goodData._2.value
    var totalRequests = 0.0
    var totalStocks = 8.0
    for (p <- goodData._1.totalProducts) {
      val coeff = getWeighting(p._1).coeff
      totalStocks += p._2.value*coeff
      totalRequests += p._3.value*coeff
    }
    if (totalStocks > 0) {
      val newMultiplier = (0.5*(prevMultiplier + totalRequests/totalStocks)).max(10.0)
      goodData._2.set(newMultiplier)
    }
    else goodData._2.set(10.0)
  }

  def updateMultiplier(good: EconomicGood) = {
    val goodData = getGoodData(good)
    computeMultiplier(goodData)
  }

  def initWeightings(structuresList: ListBuffer[Structure]) = {
    structuresList.foreach(newWeighting)
  }

  def newWeighting(s: Structure) = {
    val trash = getWeighting(s)
  }

  def updateWeightings() = {
    for (w <- weightings) {
      if (w.structure == this) w.coeff = 1.0
      else {
        try {
          townManager.determineRailwayDistance(this, w.structure) match {
            case Some(i) => w.coeff = 1/i
            case None => {
              val d = townManager.determineEuclidianDistance(this, w.structure)
              w.coeff = 1/(Math.pow(d,2))
            }
          }
        } catch {
          case e: Exception => w.coeff = 0
        }
      }
    }
    normalize(weightings)
  }

  def normalize(weightings: ListBuffer[Weighting]) = {
    var sum = 0.0
    weightings.foreach(w => sum += w.coeff)
    weightings.foreach(w => w.coeff /= sum)
  }

  def newEconomicGood(good: EconomicGood) = {
    val i = getGoodData(good)
  }

  def getMultiplier(good: Good) = getGoodData(townManager.getEconomicGood(good))._2

  def updateEconomy() = {
    goods.foreach(computeMultiplier)
    updateWeightings()
  }
}

// here -> stocks/requests from all cities
class EconomicGood(val kind: Good) {
  var totalProducts = new ListBuffer[(Structure, IntegerProperty, IntegerProperty)]

  def newEmergence(structure: Structure, stocks: IntegerProperty, requests: IntegerProperty) = {
    var i = 0
    while (i < totalProducts.length && totalProducts(i)._1 != structure) i += 1
    if (i == totalProducts.length) {
      val stockSaved = new IntegerProperty
      stockSaved <== stocks
      val requestSaved = new IntegerProperty
      requestSaved <== requests
      totalProducts += new Tuple3(structure, stockSaved, requestSaved)
    }
  }
}

class Weighting(val structure: Structure, d: Double) {
  var coeff = d
}
