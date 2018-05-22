package tycoon.objects.structure

import scala.collection.mutable.ListBuffer
import scala.Array

import tycoon.game._
import tycoon.objects.structure._

import tycoon.ui.Tile

import scalafx.beans.property.{IntegerProperty, StringProperty}


case class LargeTown(pos: GridLocation, id: Int, townManager: TownManager, owner: Player) extends Town(pos, id, townManager, owner) {
  max_population = 100000
  population = 5000 + r.nextInt(5000)
}
