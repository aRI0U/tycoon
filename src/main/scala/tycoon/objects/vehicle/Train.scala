package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import tycoon.objects.carriage._
import tycoon.objects.railway._
import tycoon.objects.structure._

abstract class Train(road : Road) extends Vehicle(road) {
  var carriagesList : ListBuffer[Carriage]
}
