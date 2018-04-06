package tycoon.objects.good

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
}
