package tycoon.objects.structure

import scala.collection.mutable.ListBuffer

import tycoon.objects.vehicle._
import tycoon.ui.Entity
import tycoon.game.GridLocation

import scalafx.beans.property.{IntegerProperty, StringProperty}


abstract class Structure(pos: GridLocation, id: Int) extends Entity {
  val structure_id = id
  var list_trains = new ListBuffer[Train]()

  //Methods
  protected val _name = StringProperty("structure name")
  def name : String = _name.get()
  def position : GridLocation = pos

  //Train manipulation
  def addTrain(train:Train) {
    list_trains += train
  }
  def removeTrain(train:Train) {
    list_trains -= train
  }
  def getTrain() : Option[Train] = {
    if (list_trains.isEmpty) None
    else Some(list_trains.last)
  }
  setPos(pos)
}
