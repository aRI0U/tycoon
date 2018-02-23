package vehicle

import scala.collection.mutable.ListBuffer

import scalafx.scene.image.Image

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
  var tileset = new Image("file:src/main/resources/tileset.png")
  var width = 32
  var height = 32
  var tileset_x = 32
  var tileset_y = 32
}
