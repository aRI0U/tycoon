package tycoon.objects.good

class Merchandise(val kind: Good, var quantity: Int) {
  var pricePerUnity : Int = 0 // use an economic model
  val productionDate : Int = 0 // get game_time

  def initExpiryDate() : Option[Int] = {
    kind match {
      case f: Food => Some(productionDate + f.storageTime)
      case _ => None
    }
  }

  var expiryDate : Option[Int] = initExpiryDate()
}
