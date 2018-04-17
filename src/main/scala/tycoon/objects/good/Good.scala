package tycoon.objects.good

import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

import tycoon.objects.structure.EconomicAgent

import scalafx.beans.property.{DoubleProperty, IntegerProperty}

abstract class Good(val label: String, priceDb: Double, val size: Double, val liquid: Boolean) {
  val totalProducts = new ListBuffer[(EconomicAgent, IntegerProperty, IntegerProperty)]

  def newEmergence(s: EconomicAgent, stocks: IntegerProperty, requests: IntegerProperty) = {
    val stocksSaved = new IntegerProperty
    val requestsSaved = new IntegerProperty
    stocksSaved <== stocks
    requestsSaved <== requests
    totalProducts += new Tuple3(s, stocksSaved, requestsSaved)
  }

  val price = DoubleProperty(priceDb)
}

case class RawMaterial(override val label: String, priceDb: Double, override val size: Double, override val liquid: Boolean) extends Good(label, priceDb, size, liquid) { }

class PreciousMetal(override val label: String, priceDb: Double, override val size: Double, override val liquid: Boolean) extends RawMaterial(label, priceDb, size, liquid) { }

case class ProcessedGood(override val label: String, priceDb: Double, override val size: Double, override val liquid: Boolean) extends Good(label, priceDb, size, liquid) { }

case class Food(override val label: String, priceDb: Double, override val size: Double, override val liquid: Boolean, val storageTime: Double, val nutritiousness: Double, val packaging: Good) extends Good(label, priceDb, size, liquid) { }

class Product() {}

object Product {
  // RawMaterial
  val Coal = new RawMaterial("Coal", 1, 1, false)
  val Iron = new RawMaterial("Iron", 1, 1, false)
  val Sand = new RawMaterial("Sand", 1, 1, false)
  val Oil = new RawMaterial("Oil", 10, 1, true)
  val Wood = new RawMaterial("Wood", 1, 1, false)
  val Leather = new RawMaterial("Leather", 5, 1, false)

  // PreciousMetal
  val Gold = new RawMaterial("Gold", 25, 1, false)

  // ProcessedGood
  val Cardboard = new ProcessedGood("Cardboard", 1, 1, false)
  val Glass = new ProcessedGood("Glass", 5, 1, false)
  val Hat = new ProcessedGood("Hat",50,5,false)
  val Plastic = new ProcessedGood("Plastic", 5, 1, false)
  val RabbitFoot = new ProcessedGood("rabbitFoot",10,1,false)
  val Revolver = new ProcessedGood("Revolver",100,5,false)
  val Ring = new ProcessedGood("Ring",1000,1,false)

  // Food
  val Cake = new Food("Cake", 7, 0.5, false, 150, 10, Cardboard)
  val Cheese = new Food("Cheese", 6, 1, false, 500, 5, Plastic)
  val PopCorn = new Food("PopCorn", 1, 0.1, false, 20, 2, Iron)
  val Corn = new Food("Corn", 1, 0.1, false, 200, 1, Iron)
  val Egg = new Food("Egg", 1, 1, false, 50, 1, Cardboard)
  val Milk = new Food("Milk", 1, 1, true, 20, 1, Glass)

  val foods = ListBuffer[Food](Cake, Milk, Egg)
  val purchases = ListBuffer[ProcessedGood](Revolver)
}
