package tycoon.objects.structure

import scala.collection.mutable.ListBuffer
import scala.Array

import tycoon.game._
import tycoon.objects.structure._

import tycoon.ui.Tile

import scalafx.beans.property.{IntegerProperty, StringProperty}


case class SmallTown(pos: GridLocation, id: Int, townManager: TownManager, override val owner: Player) extends Town(pos, id, townManager, owner) {
  max_population = 1000
  population = 50 + r.nextInt(50)
}
