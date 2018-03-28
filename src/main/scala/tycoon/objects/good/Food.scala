package tycoon.objects.good

case class Food(name: String) extends Good(name) {
  def initStorageTime() : Int = {
    label match {
      case _ => 0
    }
  }
  val storageTime = initStorageTime()
}
