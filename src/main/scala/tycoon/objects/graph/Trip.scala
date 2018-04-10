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

  var i: Int = 0
  var roadPositions = new ListBuffer[GridLocation]()

  def start() = {
    origin.removeVehicle(veh)
    for (p <- roadPositions)
    println(p.col, p.row)
    i = 0
  }

  def update (dt: Double) {
    if (active) {

      veh match {
        case plane: Plane => {
          val dirs = plane.getDirs(plane.gridPos, destination.gridPos)
          if (!plane.gridPos.eq(destination.gridPos)) {
            if (dirs.length == 2) {
              val hSpeed: Double = plane.speed.value / Math.sqrt(1 + Math.pow(plane.gridPos.adjustedRow - destination.gridPos.row, 2) / Math.pow(plane.gridPos.adjustedCol - destination.gridPos.col, 2))
              val vSpeed: Double = plane.speed.value / Math.sqrt(1 + Math.pow(plane.gridPos.adjustedCol - destination.gridPos.col, 2) / Math.pow(plane.gridPos.adjustedRow - destination.gridPos.row, 2))
              dirs foreach { dir: Direction => dir match {
                case East | West => plane.move(plane.gridPos, dir, dt, hSpeed)
                case South | North => plane.move(plane.gridPos, dir, dt, vSpeed)
                case Undefined => ()
              }}
            } else if (dirs.length == 1) {
              plane.move(plane.gridPos, dirs(0), dt, plane.speed.value)
            }
          } else {
            plane.stabilize(plane.gridPos, dt, plane.speed.value)
          }
        }

        case truck: Truck => {
          val dir = truck.getDirs(truck.gridPos, roadPositions(i + 1))(0)
          if (!truck.gridPos.eq(destination.gridPos)) {
            if (truck.move(truck.gridPos, dir, dt, truck.speed.value))
              i += 1
          }
          else
            truck.stabilize(truck.gridPos, dt, truck.speed.value)
        }
      }

      if (!active) {
        destination.addVehicle(veh)
        if (repeated) {
          val tmp: Structure = origin
          origin = destination
          destination = tmp
          origin.removeVehicle(veh)
        }
      }
    }
  }
}
