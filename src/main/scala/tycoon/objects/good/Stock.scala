package tycoon.objects.good

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._

import scalafx.beans.property.{IntegerProperty, DoubleProperty, StringProperty}

class Stock(s: Structure) {
  var productsTypes = new ListBuffer[Good]
  var datedProducts = new ListBuffer[ListBuffer[Merchandise]]
  var stocksInt = new ListBuffer[IntegerProperty]
  var requestsInt = new ListBuffer[IntegerProperty]
  var productsInt = new ListBuffer[IntegerProperty]
  var productsStr = new ListBuffer[StringProperty]
  var pricesInt = new ListBuffer[DoubleProperty]
  var pricesStr = new ListBuffer[StringProperty]
  var printablesStr = new ListBuffer[StringProperty]

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
      pricesInt += DoubleProperty(0.0)
      pricesStr += new StringProperty
      pricesStr.last <== pricesInt.last.asString
      s match {
        case t: Town => {
          printablesStr += new StringProperty
          //printablesStr.last <== productsStr.last.concat(" for $").concat(pricesStr.last)
          s.printData(1).newTownProduct(kind.label, productsInt.last, pricesInt.last)
        }
        case _ => s.printData(1).newRankedElement(kind.label, productsInt.last)
      }

    }
    else {
      if (quantity >= 0) setStocks(i, stocks(i)+quantity)
      else setRequests(i, requests(i)-quantity)
    }
    debugStocks("newProduct")
  }

  def getIndex(good: Good) : Int = productsTypes.indexOf(good)

  def getMerchandiseWIndex(m: Merchandise, i: Int) = {
    datedProducts(i) += m
    setStocks(i, stocks(i)+m.quantity)
    debugStocks("getMerchandise")
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
    debugStocks("removeMerchandise")
  }

  def receiveMerchandises(kind: Good, giver: ListBuffer[Merchandise], quantity: Option[Int]) = {
    var i = getIndex(kind)
    if (i == -1) {
      i = productsTypes.length
      newProduct(kind, 0)
    }
    println("breceive > stocks: "+stocks(i))
    println("datedProducts: "+datedProducts)
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
    debugStocks("receiveMerchandises")
    updateStocksWIndex(i)
    println("ereceive > stocks: "+stocks(i))
  }

  def giveMerchandisesWIndex(i: Int, kind: Good, receiver: ListBuffer[Merchandise], quantity: Int, condition: (Merchandise => Boolean) = (_ => true)) = {
    println("bgive > stocks: "+stocks(i))
    var notGiven = quantity
    println("quantity to trade: " + quantity)
    for (m <- datedProducts(i)) {
      if (notGiven > 0 && m.kind == kind && condition(m)) {
        println(notGiven)
        notGiven -= m.trade(datedProducts(i), receiver, Some(notGiven))
        println("just took "+notGiven+" on "+stocks(i))
      }
    }
    debugStocks("giveMerchandisesWIndex")
    updateStocksWIndex(i)
    println("egive > stocks: "+stocks(i))
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
    debugStocks("updateExpiredProducts")
    updateStocks()
  }

  def updateStocksWIndex(i: Int) = {
    println("US > initial stock:"+stocks(i))
    var sum = 0
    datedProducts(i).foreach(m => sum += m.quantity)
    println("US > sum: "+sum)
    setStocks(i, sum)
    debugStocks("updateStocksWIndex")
  }

  def updateStocks() = {
    for (i <- 0 to stocksInt.length-1) updateStocksWIndex(i)
  }

  def debugStocks(s: String) {
    if (productsTypes.length != datedProducts.length) {
      println(s)
      println("datedProducts: "+datedProducts)
      println("productsTypes: "+productsTypes)
      throw new IllegalArgumentException
    }
  }
}
