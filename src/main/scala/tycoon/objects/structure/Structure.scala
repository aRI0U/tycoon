package tycoon.objects.structure

import scala.collection.mutable.ListBuffer
import scala.util.Random

import tycoon.objects.vehicle._
import tycoon.objects.vehicle.train._
import tycoon.objects.good._
import tycoon.ui.Renderable
import tycoon.game.GridLocation

import scalafx.beans.property.{IntegerProperty, StringProperty}


abstract class Structure(pos: GridLocation, id: Int) extends Renderable(pos) {
  val structureId = id
  var trainList = new ListBuffer[Train]()
  var planeList = new ListBuffer[Plane]()
  var boatList = new ListBuffer[Boat]()
  var truckList = new ListBuffer[Truck]()

  protected val r = scala.util.Random

  protected val _name = StringProperty("")
  def name: String = _name.value
  def setName(s : String) = _name.set(s)

  var internTime: Double = 0
  def update(dt: Double)

  def addTrain(train: Train) = trainList += train
  def addPlane(plane: Plane) = planeList += plane
  def addBoat(boat: Boat) = boatList += boat
  def addTruck(truck: Truck) = truckList += truck
  def removeTrain(train: Train) = trainList -= train
  def getTrain: Option[Train] = trainList.lastOption
  def getPlane: Option[Plane] = planeList.lastOption

  val stock = new Stock(this)
}
