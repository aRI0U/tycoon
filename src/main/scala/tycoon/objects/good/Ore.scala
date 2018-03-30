package tycoon.objects.good

case class Ore(name: String) extends Good(name) {
  def initFormat() : String = {
    label match {
      case _ => "Dry"
    }
  }
  val format = initFormat()
}
