package tycoon.objects.good

abstract class Good(val label: String) {

  def initSize() :Double = {
    label match {
      case _ => 1
    }
  }
  val size = initSize()

  // weight must be strictly positive otherwise it creates ArithmeticException
  def initWeight() : Double = {
    label match {
      case _ => 1
    }
  }
  val weight = initWeight()

  def initLiquid() : Boolean = {
    label match {
      case _ => false
    }
  }
  val liquid = initLiquid()

  def initPackage() : Good = {
    label match {
      case _ => new Ore("Iron")
    }
  }
  val packaging = initPackage()
}
