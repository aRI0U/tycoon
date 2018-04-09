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


  val SmallTown = new BuyableStruct("Small Town", 50, Tile.town, newSmallTown)
  val MediumTown = new BuyableStruct("Medium Town", 100, Tile.town, newMediumTown)
  val LargeTown = new BuyableStruct("Large Town", 200, Tile.town, newLargeTown)
  val Mine = new BuyableStruct("Mine", 50, Tile.mine, newMine)
  val Farm = new BuyableStruct("Farm", 100, Tile.farm1, newFarm)
  val Factory = new BuyableStruct("Factory", 150, Tile.factory, newFactory)
  val PackingPlant = new BuyableStruct("Packing Plant", 170, Tile.factory, newPackingPlant)
  val Airport = new BuyableStruct("Airport", 1000, Tile.airport, newAirport)
  val Field = new BuyableStruct("Field", 100, Tile.field1, newField)
  val Dock = new BuyableStruct("Dock", 100, Tile.dock, newDock)
}


case class BuyableRoad(override val name: String, override val price: Int, override val tile: Tile, val newInstance: GridLocation => Rail)
extends BuyableItem(name, price, tile) {
  createByDragging = true
}

object BuyableRoad {
  def newRail(pos: GridLocation): Rail = new Rail(pos)

  val Rail = new BuyableRoad("Rail", 5, Tile.straightRailBT, newRail)
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

  val Train = new BuyableVehicle("Train", 125, Tile.locomotiveB, newTrain)
  val Plane = new BuyableVehicle("Plane", 300, Tile.plane, newPlane)
  val Boat = new BuyableVehicle("Boat", 500, Tile.boat, newBoat)
  val Truck = new BuyableVehicle("Truck", 70, Tile.truck, newTruck)
}
