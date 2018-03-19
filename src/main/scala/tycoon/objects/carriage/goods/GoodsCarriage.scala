package tycoon.objects.carriage

import scala.collection.mutable.ListBuffer

import tycoon.objects.carriage._
import tycoon.objects.structure._
import tycoon.objects.good._
import tycoon.ui.Entity
import tycoon.ui.Tile

abstract case class GoodsCarriage() extends Carriage {
  val max_transport : Int
  var transport : ListBuffer[Good]

  // override def embark(facility: Facility) : Unit = {
  // }h
  //
  // override def debark(facility: Facility) = {
  // }
}
