package tycoon.objects.structure

import scala.collection.mutable.ListBuffer

import tycoon.objects.vehicle._
import tycoon.ui.{Renderable, Printable}
import tycoon.GridLocation


abstract class Structure(pos: GridLocation, id: Int) extends Renderable {
  val structure_id = id
  var list_trains = new ListBuffer[Train]()
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
}
