package tycoon.objects.structure

import scala.collection.mutable.ListBuffer
import scala.Array

import tycoon.game.GridLocation
import tycoon.game.{Game, TownManager}
import tycoon.objects.structure._

import tycoon.ui.Tile

import scalafx.beans.property.{IntegerProperty, StringProperty}


case class SmallTown(pos: GridLocation, id: Int, townManager: TownManager) extends Town(pos, id, townManager) {
  max_population = 1000
  population = 50 + r.nextInt(50)
}
