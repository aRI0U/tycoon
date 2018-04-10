package tycoon.game


import scalafx.Includes._
import scalafx.geometry.Pos
import scalafx.scene.control.{Label, Tab, TabPane}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox, Priority}
import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.ui.Tile
import tycoon.objects.vehicle._
import tycoon.objects.vehicle.train._


sealed abstract class BuyableItem(val name: String, val price: Int, val tile: Tile) {
  private val formatter = java.text.NumberFormat.getIntegerInstance

  def priceStr: String = "$" + formatter.format(price)

  var createByDragging: Boolean = false
}


case class BuyableStruct(override val name: String, override val price: Int, override val tile: Tile, val newInstance: (GridLocation, Int, TownManager) => Structure)
extends BuyableItem(name, price, tile) {

}


object BuyableStruct {
  def newSmallTown(pos: GridLocation, id: Int, townManager: TownManager): SmallTown = new SmallTown(pos, id, townManager)
  def newMediumTown(pos: GridLocation, id: Int, townManager: TownManager): MediumTown = new MediumTown(pos, id, townManager)
  def newLargeTown(pos: GridLocation, id: Int, townManager: TownManager): LargeTown = new LargeTown(pos, id, townManager)
  def newMine(pos: GridLocation, id: Int, townManager: TownManager): Mine = new Mine(pos, id, townManager)
  def newFarm(pos: GridLocation, id: Int, townManager: TownManager): Farm = new Farm(pos, id, townManager)
  def newFactory(pos: GridLocation, id: Int, townManager: TownManager): Factory = new Factory(pos, id, townManager)
  def newPackingPlant(pos: GridLocation, id: Int, townManager: TownManager) : PackingPlant = new PackingPlant(pos, id, townManager)
  def newAirport(pos: GridLocation, id: Int, townManager: TownManager): Airport = new Airport(pos, id)
  def newField(pos: GridLocation, id: Int, townManager: TownManager): Field = new Field(pos, id)
  def newDock(pos: GridLocation, id: Int, townManager: TownManager): Dock = new Dock(pos, id)

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
