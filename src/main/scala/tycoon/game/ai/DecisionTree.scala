package tycoon.game.ai

import scala.Array
import scala.collection.immutable.List
import scala.util.Random

import tycoon.game._

sealed abstract class DecisionTree

case class Node(val sons : Array[DecisionTree]) extends DecisionTree

sealed abstract class Leaf extends DecisionTree

case class BuyStruct(s: BuyableStruct) extends Leaf
case class BuyVehicle(v: BuyableVehicle) extends Leaf
case class BuyRoad(r: BuyableRoad) extends Leaf

object BuyStruct {
  val SmallTown = new BuyStruct(BuyableStruct.SmallTown)
  val MediumTown = new BuyStruct(BuyableStruct.MediumTown)
  val LargeTown = new BuyStruct(BuyableStruct.LargeTown)

  val WindMill = new BuyStruct(BuyableStruct.WindMill)

  val Airport = new BuyStruct(BuyableStruct.Airport)
  val Dock = new BuyStruct(BuyableStruct.Dock)
}

object BuyVehicle {
  val Train = new BuyVehicle(BuyableVehicle.Train)
  val Boat = new BuyVehicle(BuyableVehicle.Boat)
  val Plane = new BuyVehicle(BuyableVehicle.Plane)
  val Truck = new BuyVehicle(BuyableVehicle.Train)
}

object BuyRoad {
  val Rail = new BuyRoad(BuyableRoad.Rail)
  val Way = new BuyRoad(BuyableRoad.Asphalt)
  val Canal = new BuyRoad(BuyableRoad.Water)
  val Flight = new BuyStruct(BuyableStruct.Airport)
}

object Node {
  val BuyTownNode = new Node(Array(BuyStruct.SmallTown, BuyStruct.MediumTown, BuyStruct.LargeTown))

  val BuyFacilityNode = new Node(Array(BuyStruct.WindMill))

  val BuyStructNode = new Node(Array(BuyTownNode, BuyFacilityNode))

  val BuyVehicleNode = new Node(Array(BuyVehicle.Train, BuyVehicle.Boat, BuyVehicle.Plane, BuyVehicle.Truck))

  val TownToTownNode = new Node(Array(BuyRoad.Rail, BuyRoad.Way, BuyRoad.Canal, BuyRoad.Flight))

  val TownToFacilityNode = new Node(Array(BuyRoad.Rail, BuyRoad.Way))

  val ConnectionNode = new Node(Array(TownToTownNode, TownToFacilityNode))

  val Root = new Node(Array(BuyVehicle.Plane))
}
