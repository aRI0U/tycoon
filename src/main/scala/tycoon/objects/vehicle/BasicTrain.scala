package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import scalafx.scene.image.Image

import tycoon.ui.Tile
import tycoon.ui.Sprite
import tycoon.GridLocation
import tycoon.Game
import tycoon.ui.DraggableTiledPane

import tycoon.objects.carriage._
import tycoon.objects.railway._
import tycoon.objects.structure._

class BasicTrain(town : Town, nb_carriages : Int) extends Train(town, nb_carriages) {

  var visible = false
  var speed = 10
  var destination_x = 0
  var destination_y = 0
  val weight = 50
  val cost = 200

  var current_rail : Option[Rail] = None
  //var trail = road.rails
  val tile = new Tile(Sprite.tile_locomotive)

  var carriages_list = new ListBuffer[Carriage]()
  add_carriage()

  var pos : GridLocation = location match {
    case Some(structure : Town) => {
      new GridLocation(structure.position.get_x +1,structure.position.get_y)
    }
    case Some(structure ) => structure.position
    case None => current_rail match {
      case Some(rail) => rail.position
      case None => new GridLocation(0,0) // not supposed to happen
    }
  }
  gridLoc = pos
}
