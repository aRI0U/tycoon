package tycoon.game.ai

import scala.Array
import scala.collection.immutable.List
import scala.util.Random

import tycoon.game._
import tycoon.ui.Tile

sealed abstract class DecisionTree

case class Node(val sons : Array[DecisionTree]) extends DecisionTree

sealed abstract class Leaf extends DecisionTree

case class BuyStruct(s: BuyableStruct) extends Leaf
case class BuyVehicle(v: BuyableVehicle) extends Leaf
case class BuyRoad(r: BuyableItem) extends Leaf
case class OtherAction(s: String) extends Leaf
case class RouteLeaf(l: List[GridLocation], r: BuyableRoad) extends Leaf
case class TripLeaf(v: BuyableVehicle) extends Leaf


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
  val Flight = new BuyRoad(BuyableStruct.Airport)
}

object OtherAction {
  val TownToTown = new OtherAction("tt")
  val TownToFacility = new OtherAction("tf")
  val FacilityToFacility = new OtherAction("ff")
  val Trip = new OtherAction("trip")
}

object Node {
  val BuyTownNode = new Node(Array(BuyStruct.SmallTown, BuyStruct.MediumTown, BuyStruct.LargeTown))

  val BuyFacilityNode = new Node(Array(BuyStruct.WindMill))

  val BuyStructNode = new Node(Array(BuyTownNode, BuyFacilityNode))

  val BuyVehicleNode = new Node(Array(BuyVehicle.Train, BuyVehicle.Boat, BuyVehicle.Plane, BuyVehicle.Truck))

  val TownToTownNode = new Node(Array(BuyRoad.Rail, BuyRoad.Way, BuyRoad.Canal, BuyRoad.Flight))

  val TownToFacilityNode = new Node(Array(BuyRoad.Rail, BuyRoad.Way))

  val FacilityToFacilityNode = new Node(Array(BuyRoad.Rail, BuyRoad.Way))

  val ConnectionNode = new Node(Array(TownToTownNode, TownToFacilityNode, FacilityToFacilityNode))

  val Root = new Node(Array(BuyStructNode, BuyVehicleNode, ConnectionNode, OtherAction.Trip))

}
