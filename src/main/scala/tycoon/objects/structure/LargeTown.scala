package tycoon.objects.structure

import scala.collection.mutable.ListBuffer
import scala.Array

import tycoon.game.GridLocation
import tycoon.game.{Game, TownManager}
import tycoon.objects.structure._

import tycoon.ui.Tile

import scalafx.beans.property.{IntegerProperty, StringProperty}


case class LargeTown(pos: GridLocation, id: Int) extends Town(pos, id) {
  max_population = 100000
  population = 5000 + r.nextInt(5000)
}
