package tycoon.objects.good

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._

import scalafx.beans.property.{IntegerProperty, DoubleProperty, StringProperty}

class Stock(s: Structure) {
  var productsTypes = new ListBuffer[Good]
  var stocks = new ListBuffer[Int]
  var datedProducts = new ListBuffer[ListBuffer[Merchandise]]
  var productsInt = new ListBuffer[IntegerProperty]
  var productsStr = new ListBuffer[StringProperty]
  var pricesInt = new ListBuffer[DoubleProperty]
  var pricesStr = new ListBuffer[StringProperty]
  var printablesStr = new ListBuffer[StringProperty]

  // usual methods
  def product(i: Int) : Int = productsInt(i).value

  def addProduction(i: Int, amount: Int) = productsInt(i).set(product(i) + amount)

  def newProduct(kind: Good, quantity: Int) = {
    val i = productsTypes.indexOf(kind)
    if (i == -1) {
      productsTypes += kind
      stocks += (quantity.max(0))
      datedProducts += new ListBuffer[Merchandise]
      productsInt += IntegerProperty(quantity)
      productsStr += new StringProperty
      productsStr.last <== productsInt.last.asString
      pricesInt += DoubleProperty(0)
      pricesStr += new StringProperty
      pricesStr.last <== pricesInt.last.asString
      s match {
        case t: Town => {
          printablesStr += new StringProperty
          printablesStr.last <== productsStr.last.concat(" for $").concat(pricesStr.last)
          s.printData(1).data += new Tuple2(kind.label, printablesStr.last)
        }
        case _ => s.printData(1).data += new Tuple2(kind.label, productsStr.last)
      }

    }
    else {
      addProduction(i, quantity)
    }
  }

  def getIndex(good: Good) : Int = productsTypes.indexOf(good)

  def getMerchandiseWIndex(m: Merchandise, i: Int) = {
    datedProducts(i) += m
    stocks(i) += m.quantity
    addProduction(i, m.quantity)
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
    stocks(i) -= m.quantity
    addProduction(i, -m.quantity)
  }

  def receiveMerchandises(kind: Good, giver: ListBuffer[Merchandise], quantity: Int) = {
    var i = getIndex(kind)
    if (i == -1) {
      i = productsTypes.length
      newProduct(kind, 0)
    }
    var notReceived = quantity
    for (m <- giver) {
      if (m.kind == kind) {
        notReceived -= m.trade(giver, datedProducts(i), notReceived)
      }
    }
    updateStocks()
  }

  def giveMerchandisesWIndex(i: Int, kind: Good, receiver: ListBuffer[Merchandise], quantity: Int, condition: (Merchandise => Boolean) = (_ => true)) = {
    var notGiven = quantity
    for (m <- datedProducts(i)) {
      if (notGiven > 0 && m.kind == kind && condition(m)) {
        notGiven -= m.trade(datedProducts(i), receiver, notGiven)
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

  def updateExpiredProducts(currentTime: Double) = {
    for (merchList <- datedProducts) {
      for (m <- merchList) {
        m.expiryDate match {
          case Some(time) => {
            if (currentTime > time) merchList -= m
          }
          case None => ()
        }
      }
    }
    updateStocks()
  }

  def updateStocksWIndex(i: Int) = {
    val s = stocks(i)
    var sum = 0
    datedProducts(i).foreach(m => sum += m.quantity)
    addProduction(i, sum - s)
    stocks(i) = s
  }

  def updateStocks() = {
    for (i <- 0 to stocks.length-1) updateStocksWIndex(i)
  }
}
