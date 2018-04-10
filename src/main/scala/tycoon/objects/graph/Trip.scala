package tycoon.objects.graph

import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.ui.Tile
import tycoon.objects.vehicle.train._
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle._
import tycoon.game.Game
import tycoon.game.GridLocation



class Trip(var origin: Structure, var destination: Structure, val veh: Vehicle, var repeated: Boolean) {

  def active: Boolean = (!veh.gridPos.eq(destination.gridPos) || veh.gridPos.percentageWidth != 0 || veh.gridPos.percentageHeight != 0)

  def start() = {
    origin.removeVehicle(veh)
  }

  def update (dt: Double) {
    println("update")
    if (active) {
      println("active")

      veh match {
        case veh: Plane => {
          val dirs = veh.getDirs(veh.gridPos, destination.gridPos)
          if (!veh.gridPos.eq(destination.gridPos)) {
            if (dirs.length == 2) {
              val hSpeed: Double = veh.speed.value / Math.sqrt(1 + Math.pow(veh.gridPos.adjustedRow - destination.gridPos.row, 2) / Math.pow(veh.gridPos.adjustedCol - destination.gridPos.col, 2))
              val vSpeed: Double = veh.speed.value / Math.sqrt(1 + Math.pow(veh.gridPos.adjustedCol - destination.gridPos.col, 2) / Math.pow(veh.gridPos.adjustedRow - destination.gridPos.row, 2))
              dirs foreach { dir: Direction => dir match {
                case East | West => veh.move(veh.gridPos, dir, dt, hSpeed)
                case South | North => veh.move(veh.gridPos, dir, dt, vSpeed)
                case Undefined => ()
              }}
              println("woogle")
            } else if (dirs.length == 1) {
              veh.move(veh.gridPos, dirs(0), dt, veh.speed.value)
              println("straight")
            } else {
              println("nada")
            }
          } else {
            veh.stabilize(veh.gridPos, dt, veh.speed.value)
          }
        }
      }

      if (!active) {
        println("arrived")
        destination.addVehicle(veh)
        if (repeated) {
          println("repeat")
          val tmp: Structure = origin
          origin = destination
          destination = tmp
          origin.removeVehicle(veh)
        }
      }
    }
  }
}
