package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import scalafx.scene.image.Image

import tycoon.objects.carriage._

abstract class BasicTrain(x: Int, y: Int) extends Train(x, y) {
  var pos_x = x
  var pos_y = y
  var visible = true
  var speed = 10
  var destination_x = 0
  var destination_y = 0
  val weight = 50
  var carriagesList = new ListBuffer[Carriage]()
}
