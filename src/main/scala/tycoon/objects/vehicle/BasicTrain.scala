package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import scalafx.scene.image.Image

import tycoon.ui.Tile
import tycoon.ui.Sprite
import tycoon.GridLocation

import tycoon.objects.carriage._
import tycoon.objects.railway._
import tycoon.objects.structure._

class BasicTrain(road : Road) extends Train(road) {
  // x: Int, y: Int
  val init_town = road.start_town.get
  var init_pos : GridLocation = init_town.position
  // var pos_y = y
  var visible = true
  var speed = 10
  var destination_x = 0
  var destination_y = 0
  val weight = 50
  var trail = new ListBuffer[Rail]()
  val tile = new Tile(Sprite.tile_locomotive)
  var carriagesList = new ListBuffer[Carriage]()
  // gridLoc = (x,y)
}
