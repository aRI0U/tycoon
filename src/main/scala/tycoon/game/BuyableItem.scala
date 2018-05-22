package tycoon.game

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.ui.Tile
import tycoon.objects.vehicle._
import tycoon.objects.vehicle.train._

import scalafx.Includes._
import scalafx.geometry.Pos
import scalafx.scene.control.{Label, Tab, TabPane}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox, Priority}


sealed abstract class BuyableItem(val name: String, val price: Int, val tile: Tile) {
  private val formatter = java.text.NumberFormat.getIntegerInstance

  def priceStr: String = "$" + formatter.format(price)

  var createByDragging: Boolean = false
}


case class BuyableStruct(override val name: String, override val price: Int, override val tile: Tile, val newInstance: (GridLocation, Int, TownManager, Player) => Structure)
extends BuyableItem(name, price, tile) {
  createByDragging = false
}

object BuyableStruct {
  def newSmallTown(pos: GridLocation, id: Int, townManager: TownManager, player: Player): SmallTown = new SmallTown(pos, id, townManager, player)
  def newMediumTown(pos: GridLocation, id: Int, townManager: TownManager, player: Player): MediumTown = new MediumTown(pos, id, townManager, player)
  def newLargeTown(pos: GridLocation, id: Int, townManager: TownManager, player: Player): LargeTown = new LargeTown(pos, id, townManager, player)
  def newMine(pos: GridLocation, id: Int, townManager: TownManager, player: Player): Mine = new Mine(pos, id, townManager, player)
  def newFarm(pos: GridLocation, id: Int, townManager: TownManager, player: Player): Farm = new Farm(pos, id, townManager, player)
  def newFactory(pos: GridLocation, id: Int, townManager: TownManager, player: Player): Factory = new Factory(pos, id, townManager, player)
  def newPackingPlant(pos: GridLocation, id: Int, townManager: TownManager, player: Player) : PackingPlant = new PackingPlant(pos, id, townManager, player)
  def newWindMill(pos: GridLocation, id: Int, townManager: TownManager, player: Player): WindMill = new WindMill(pos, id,townManager, player)
  def newAirport(pos: GridLocation, id: Int, townManager: TownManager, player: Player): Airport = new Airport(pos, id, player)
  def newField(pos: GridLocation, id: Int, townManager: TownManager, player: Player): Field = new Field(pos, id, player)
  def newDock(pos: GridLocation, id: Int, townManager: TownManager, player: Player): Dock = new Dock(pos, id, player)

  val SmallTown = new BuyableStruct("Small Town", Settings.CostSmallTown, Tile.Town, newSmallTown)
  val MediumTown = new BuyableStruct("Medium Town", Settings.CostMediumTown, Tile.Town, newMediumTown)
  val LargeTown = new BuyableStruct("Large Town", Settings.CostLargeTown, Tile.Town, newLargeTown)
  val Mine = new BuyableStruct("Mine", Settings.CostMine, Tile.Mine, newMine)
  val Farm = new BuyableStruct("Farm", Settings.CostFarm, Tile.Farm(0), newFarm)
  val Factory = new BuyableStruct("Factory", Settings.CostFactory, Tile.Factory, newFactory)
  val PackingPlant = new BuyableStruct("Packing Plant", Settings.CostPackingPlant, Tile.Factory, newPackingPlant)
  val Airport = new BuyableStruct("Airport", Settings.CostAirport, Tile.Airport, newAirport)
  val Field = new BuyableStruct("Field", Settings.CostField, Tile.Field(0), newField)
  val Dock = new BuyableStruct("Dock", Settings.CostDock, Tile.Dock, newDock)
  val WindMill = new BuyableStruct("WindMill", Settings.CostWindMill, Tile.Wind, newWindMill)
}


case class BuyableRoad(override val name: String, override val price: Int, override val tile: Tile, val newInstance: GridLocation => RoadItem)
extends BuyableItem(name, price, tile) {
  createByDragging = true
}

object BuyableRoad {
  def newRail(pos: GridLocation): Rail = new Rail(pos)
  def newAsphalt(pos: GridLocation): Asphalt = new Asphalt(pos)
  def newGrass(pos: GridLocation): Grass = new Grass(pos)
  def newWater(pos: GridLocation): Water = new Water(pos)

  val Rail = new BuyableRoad("Rail", Settings.CostRail, Tile.StraightRailBT, newRail)
  val Asphalt = new BuyableRoad("Asphalt", Settings.CostAsphalt, Tile.Asphalt, newAsphalt)
  val Grass = new BuyableRoad("Grass", Settings.CostGrass, Tile.Grass(0), newGrass)
  val Water = new BuyableRoad("Water", Settings.CostWater, Tile.Water(0), newWater)
}


case class BuyableVehicle(override val name: String, override val price: Int, override val tile: Tile, val newInstance: (Int, Structure, Player) => Vehicle)
extends BuyableItem(name, price, tile) {
  createByDragging = false
}

object BuyableVehicle {
  def newTrain(id: Int, struct: Structure, player: Player): Train = new Train(id, struct, player)
  def newPlane(id: Int, struct: Structure, player: Player): Plane = new Plane(id, struct, player)
  def newBoat(id: Int, struct: Structure, player: Player): Boat = new Boat(id, struct, player)
  def newTruck(id: Int, struct: Structure, player: Player): Truck = new Truck(id, struct, player)

  val Train = new BuyableVehicle("Train", Settings.CostTrain, Tile.LocomotiveB, newTrain)
  val Plane = new BuyableVehicle("Plane", Settings.CostPlane, Tile.Plane, newPlane)
  val Boat = new BuyableVehicle("Boat", Settings.CostBoat, Tile.Boat, newBoat)
  val Truck = new BuyableVehicle("Truck", Settings.CostTruck, Tile.Truck, newTruck)
}
