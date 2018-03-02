package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import tycoon.objects.carriage._

abstract class Train(x: Int, y: Int) extends Vehicle(x,y) {
  var carriagesList : ListBuffer[Carriage]
}
