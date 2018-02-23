package vehicle


import scala.collection.mutable.ListBuffer

import carriage._

class BasicTrain(x:Integer, y:Integer) extends Train(x, y) {
  var pos_x = x
  var pos_y = y
  val sprite = "bite"
  var visible = true
  var speed = 10
  var destination_x = 0
  var destination_y = 0
  val weight = 50
  var carriagesList = new ListBuffer[Carriage]()
}
