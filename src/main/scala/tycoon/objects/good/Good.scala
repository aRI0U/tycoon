package tycoon.objects.good

import scala.collection.immutable.List

abstract class Good(val label: String, val price: Double, val size: Double, val liquid: Boolean) {
  // def initSize() :Double = {
  //   label match {
  //     case _ => 1
  //   }
  // }
  // val size = initSize()
  //
  // // weight must be strictly positive otherwise it creates ArithmeticException
  // def initWeight() : Double = {
  //   label match {
  //     case _ => 1
  //   }
  // }
  // val weight = initWeight()
  //
  // def initLiquid() : Boolean = {
  //   label match {
  //     case _ => false
  //   }
  // }
  // val liquid = initLiquid()

  // def initPackage() : Good = {
  //   label match {
  //     case _ => new Good("Iron")
  //   }
  // }
  // val packaging = initPackage()
}

case class RawMaterial(override val label: String, override val price: Double, override val size: Double, override val liquid: Boolean) extends Good(label, price, size, liquid) { }

case class ProcessedGood(override val label: String, override val price: Double, override val size: Double, override val liquid: Boolean) extends Good(label, price, size, liquid) { }

case class Food(override val label: String, override val price: Double,  override val size: Double, override val liquid: Boolean, val storageTime: Double, val nutritiousness: Double, val packaging: Good) extends Good(label, price, size, liquid) { }

class Product() {}

object Product {
  // RawMaterial
  val Cardboard = new RawMaterial("Cardboard", 1, 1, false)
  val Coal = new RawMaterial("Coal", 1, 1, false)
  val Gold = new RawMaterial("Gold", 1, 1, false)
  val Iron = new RawMaterial("Iron", 1, 1, false)

  // ProcessedGood
  val Plastic = new ProcessedGood("Plastic", 1, 1, false)

  // Food
  val Cake = new Food("Cake", 7, 0.5, false, 150, 3, Cardboard)
  val Cheese = new Food("Cheese", 1, 1, false, 1, 1, Plastic)
  val Corn = new Food("Corn", 1, 1, false, 1, 1, Iron)
  val Egg = new Food("Egg", 1, 1, false, 1, 1, Cardboard)
}
