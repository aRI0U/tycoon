package tycoon.objects.good

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._

import scalafx.beans.property.{IntegerProperty, DoubleProperty, StringProperty}

class Stock(s: EconomicAgent) {
  var productsTypes = new ListBuffer[Good]
  var datedProducts = new ListBuffer[ListBuffer[Merchandise]]
  var stocksInt = new ListBuffer[IntegerProperty]
  var requestsInt = new ListBuffer[IntegerProperty]
  var productsInt = new ListBuffer[IntegerProperty]
  var pricesInt = new ListBuffer[DoubleProperty]

  // usual methods
  def stocks(i: Int) : Int = stocksInt(i).value
  def requests(i: Int) : Int = requestsInt(i).value
  def products(i: Int) : Int = productsInt(i).value

  def setStocks(i: Int, amount: Int) = {
    stocksInt(i).set(amount)
  }

  def setRequests(i: Int, amount: Int) = {
    requestsInt(i).set(amount)
  }

  def newProduct(kind: Good, quantity: Int) = {
    val i = productsTypes.indexOf(kind)
    if (i == -1) {
      productsTypes += kind
      datedProducts += new ListBuffer[Merchandise]
      if (quantity >= 0) {
        stocksInt += IntegerProperty(quantity)
        requestsInt += IntegerProperty(0)
      }
      else {
        stocksInt += IntegerProperty(0)
        requestsInt += IntegerProperty(-quantity)
      }
      productsInt += new IntegerProperty
      productsInt.last <== stocksInt.last - requestsInt.last
      pricesInt += new DoubleProperty
      pricesInt.last <== DoubleProperty(kind.price) * s.getMultiplier(kind)
      s match {
        case t: Town => {
          t.printData(1).newTownProduct(kind.label, productsInt.last, pricesInt.last)
          t.report(kind, stocksInt.last, requestsInt.last)
        }
        case f: Facility => {
          f.printData(1).newRankedElement(kind.label, productsInt.last)
          f.report(kind, stocksInt.last, requestsInt.last)
        }
        case _ => ()
      }
      s.report(kind, stocksInt.last, requestsInt.last)
    }
    else {
      if (quantity >= 0) setStocks(i, stocks(i)+quantity)
      else setRequests(i, requests(i)-quantity)
    }
  }

  def getIndex(good: Good) : Int = productsTypes.indexOf(good)

  def getMerchandiseWIndex(m: Merchandise, i: Int) = {
    datedProducts(i) += m
    setStocks(i, stocks(i)+m.quantity)
  }

  def getMerchandise(m: Merchandise) = {
    var i = getIndex(m.kind)
    if (i == -1) {
      i = productsTypes.length
      newProduct(m.kind, 0)
    }
    getMerchandiseWIndex(m, i)
  }

  def removeMerchandise(m: Merchandise, i: Int) = {
    datedProducts(i) -= m
    setStocks(i, stocks(i)-m.quantity)
  }

  def receiveMerchandises(kind: Good, giver: ListBuffer[Merchandise], quantity: Option[Int]) = {
    var i = getIndex(kind)
    if (i == -1) {
      i = productsTypes.length
      newProduct(kind, 0)
    }
    quantity match {
      case Some(q) => {
        var notReceived = q
        for (m <- giver) {
          if (m.kind == kind && notReceived > 0) {
            notReceived -= m.trade(giver, datedProducts(i), Some(notReceived))
          }
        }
      }
      case None => {
        var trash = 0
        for (m <- giver) {
          if (m.kind == kind) {
            trash -= m.trade(giver, datedProducts(i), None)
          }
        }
      }
    }
    updateStocksWIndex(i)
  }

  def giveMerchandisesWIndex(i: Int, kind: Good, receiver: ListBuffer[Merchandise], quantity: Int, condition: (Merchandise => Boolean) = (_ => true)) = {
    var notGiven = quantity
    for (m <- datedProducts(i)) {
      if (notGiven > 0 && m.kind == kind && condition(m)) {
        notGiven -= m.trade(datedProducts(i), receiver, Some(notGiven))
      }
    }
    updateStocksWIndex(i)
  }

  def giveMerchandises(kind: Good, receiver: ListBuffer[Merchandise], quantity: Int) = {
    var i = getIndex(kind)
    if (i == -1) {
      println("tycoon > objects > good > Stock: you cannot give something you don't have")
      i = productsTypes.length
      newProduct(kind, 0)
    }
    giveMerchandisesWIndex(i, kind, receiver, quantity)
    updateStocksWIndex(i)
  }

  def updateExpiredProducts(currentTime: Double) : Boolean = {
    var someExpired = false
    for (merchList <- datedProducts) {
      for (m <- merchList) {
        m.expiryDate match {
          case Some(time) => {
            if (currentTime > time) {
              merchList -= m
              someExpired = true
            }
          }
          case None => ()
        }
      }
    }
    updateStocks()
    someExpired
  }

  def updateStocksWIndex(i: Int) = {
    var sum = 0
    datedProducts(i).foreach(m => sum += m.quantity)
    setStocks(i, sum)
  }

  def updateStocks() = {
    for (i <- 0 to stocksInt.length-1) updateStocksWIndex(i)
  }
}
