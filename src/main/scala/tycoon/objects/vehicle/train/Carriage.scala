package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.game.{GridLocation, Player}

abstract class Carriage(_owner: Player) extends Renderable(new GridLocation(-1, -1)) {
  var currentRail: Option[Rail] = None
  var stops = new ListBuffer[Structure]

  def owner: Player = _owner

  def rotation(v: Int) = { } // ??



  def embark(structure: Structure, stops: ListBuffer[Structure]) : Unit = {}
  def debark(s: Structure) : Unit = {}

  val cost = 20
  var weight : Double = 200

}
