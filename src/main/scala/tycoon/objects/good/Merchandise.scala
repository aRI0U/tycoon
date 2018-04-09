package tycoon.objects.good

import scala.collection.mutable.ListBuffer

class Merchandise(val kind: Good, var quantity: Int, val productionDate : Double) {
  var pricePerUnity : Int = 0 // use an economic model

  def initExpiryDate() : Option[Double] = {
    kind match {
      case f: Food => Some(productionDate + f.storageTime)
      case _ => None
    }
  }

  var expiryDate : Option[Double] = initExpiryDate()

  var packed = false

  def trade(giver: ListBuffer[Merchandise], receiver: ListBuffer[Merchandise], limit: Option[Int]) : Int = {
    var tradedQuantity = quantity
    limit match {
      case Some(l) => if (l < quantity) tradedQuantity = l
      case None => ()
    }
    println("trade: "+tradedQuantity)
    if (tradedQuantity == quantity) {
      println("trade: not divide")
      receiver += this
      giver -= this
    }
    else {
      println("trade: divide")
      receiver += new Merchandise(kind, tradedQuantity, productionDate)
      quantity -= tradedQuantity
    }
    tradedQuantity
  }
}
