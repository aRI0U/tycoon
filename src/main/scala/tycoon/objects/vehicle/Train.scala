package vehicle

import scala.collection.mutable.ListBuffer

import carriage._

abstract class Train(x:Integer, y:Integer) extends Vehicle(x,y) {
  var carriagesList : ListBuffer[Carriage]
}
