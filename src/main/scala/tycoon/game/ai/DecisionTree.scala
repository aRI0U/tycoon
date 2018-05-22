package tycoon.game.ai

import scala.Array
import scala.util.Random

import tycoon.game._

sealed abstract class DecisionTree {

  val r = scala.util.Random

  def searchAction(t: DecisionTree = this) : Leaf = {
    t match {
      case a: Leaf => a
      case Node(sons) => searchAction(sons(r.nextInt(sons.length)))
    }
  }
}

case class Node(val sons : Array[DecisionTree]) extends DecisionTree

sealed abstract class Leaf extends DecisionTree

case class BuyStruct(s: BuyableStruct) extends Leaf
case class BuyVehicle(v: BuyableVehicle) extends Leaf

object BuyStruct {
  val SmallTown = new BuyStruct(BuyableStruct.SmallTown)
  val MediumTown = new BuyStruct(BuyableStruct.MediumTown)
  val LargeTown = new BuyStruct(BuyableStruct.LargeTown)

  val Windmill = new BuyStruct(BuyableStruct.WindMill)

  val Dock = new BuyStruct(BuyableStruct.Dock)
  val Airport = new BuyStruct(BuyableStruct.Airport)
}

object BuyVehicle {
  val Train = new BuyVehicle(BuyableVehicle.Train)
  val Boat = new BuyVehicle(BuyableVehicle.Boat)
  val Plane = new BuyVehicle(BuyableVehicle.Plane)
  val Truck = new BuyVehicle(BuyableVehicle.Train)
}

object Node {
  val BuyTownNode = new Node(Array(BuyStruct.SmallTown, BuyStruct.MediumTown, BuyStruct.LargeTown))
  val BuyStructNode = new Node(Array(BuyTownNode, BuyStruct.Windmill))

  val BuyVehicleNode = new Node(Array(BuyVehicle.Train, BuyVehicle.Boat, BuyVehicle.Plane, BuyVehicle.Truck))

  val Root = new Node(Array(BuyStructNode, BuyVehicleNode))
}
