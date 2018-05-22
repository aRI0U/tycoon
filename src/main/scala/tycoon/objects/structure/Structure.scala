package tycoon.objects.structure

import scala.collection.mutable.ListBuffer
import scala.util.Random

import tycoon.game.{GridLocation,Player}
import tycoon.objects.vehicle._
import tycoon.objects.vehicle.train._
import tycoon.objects.good._
import tycoon.ui.{PrintableData,PrintableElement,Renderable}

import scalafx.beans.property.{IntegerProperty, StringProperty}


abstract class Structure(pos: GridLocation, id: Int, owner: Player) extends Renderable(pos) {

  val structureId = id
  var trainList = new ListBuffer[Train]()
  var planeList = new ListBuffer[Vehicle]()
  var boatList = new ListBuffer[Vehicle]()
  var truckList = new ListBuffer[Vehicle]()

  def getOwner : Player = owner

  def addVehicle(v: Vehicle): Unit = {
    v match {
      case train: Train => trainList += train
      case plane: Plane => planeList += plane
      case boat: Boat => boatList += boat
      case truck: Truck => truckList += truck
      case _ => ()
    }
  }
  def removeVehicle(v: Vehicle): Unit = {
    v match {
      case train: Train => trainList -= train
      case plane: Plane => planeList -= plane
      case boat: Boat => boatList -= boat
      case truck: Truck => truckList -= truck
      case _ => ()
    }
  }

  protected val r = scala.util.Random

  protected val _name = StringProperty("")
  def name: String = _name.value
  def setName(s : String) = _name.set(s)

  var internTime: Double = 0
  def update(dt: Double)
}
