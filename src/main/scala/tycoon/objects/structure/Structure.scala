package tycoon.objects.structure

import scala.collection.mutable.ListBuffer

import tycoon.objects.vehicle._
import tycoon.ui.Renderable
import tycoon.game.GridLocation

import scalafx.beans.property.{IntegerProperty, StringProperty}


abstract class Structure(pos: GridLocation, id: Int) extends Renderable(pos) {
  val structure_id = id
  var trainList = new ListBuffer[Train]()

  protected val _name = StringProperty("")
  def name: String = _name.value

  var intern_time: Double = 0
  def update(dt: Double)

  def addTrain(train: Train) = trainList += train
  def removeTrain(train: Train) = trainList -= train
  def getTrain: Option[Train] = trainList.lastOption
}
