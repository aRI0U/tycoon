package tycoon.objects.graph

import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.ui.Tile
import tycoon.objects.vehicle.train._
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle._
import tycoon.game._
import tycoon.game.GridLocation



class Trip(var origin: Structure, var destination: Structure, val veh: Vehicle, var repeated: Boolean) {

  def active: Boolean = (!veh.gridPos.eq(destination.gridPos) || veh.gridPos.percentageWidth != 0 || veh.gridPos.percentageHeight != 0)

  var i: Int = 0
  var roadPositions = new ListBuffer[GridLocation]()

  def start() = {
    veh.boarding(ListBuffer[Structure](destination))
    veh.location = origin
    origin.removeVehicle(veh)
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
          if (i+1 < roadPositions.length && !truck.gridPos.eq(roadPositions(i+1))) {
            val dirs = truck.getDirs(truck.gridPos, roadPositions(i+1))
            if (dirs.length == 2) {
              val hSpeed: Double = truck.speed.value / Math.sqrt(1 + Math.pow(truck.gridPos.adjustedRow - roadPositions(i+1).row, 2) / Math.pow(truck.gridPos.adjustedCol - roadPositions(i+1).col, 2))
              val vSpeed: Double = truck.speed.value / Math.sqrt(1 + Math.pow(truck.gridPos.adjustedCol - roadPositions(i+1).col, 2) / Math.pow(truck.gridPos.adjustedRow - roadPositions(i+1).row, 2))
              dirs foreach { dir: Direction => dir match {
                case East | West => truck.move(truck.gridPos, dir, dt, hSpeed)
                case South | North => truck.move(truck.gridPos, dir, dt, vSpeed)
                case Undefined => ()
              }}
            } else if (dirs.length == 1) {
              truck.move(truck.gridPos, dirs(0), dt, truck.speed.value)
            }
          } else {
            if (truck.stabilize(truck.gridPos, dt, truck.speed.value))
              i += 1
          }
        }

        case boat: Boat => {
          if (i+1 < roadPositions.length && !boat.gridPos.eq(roadPositions(i+1))) {
            val dirs = boat.getDirs(boat.gridPos, roadPositions(i+1))
            if (dirs.length == 2) {
              val hSpeed: Double = boat.speed.value / Math.sqrt(1 + Math.pow(boat.gridPos.adjustedRow - roadPositions(i+1).row, 2) / Math.pow(boat.gridPos.adjustedCol - roadPositions(i+1).col, 2))
              val vSpeed: Double = boat.speed.value / Math.sqrt(1 + Math.pow(boat.gridPos.adjustedCol - roadPositions(i+1).col, 2) / Math.pow(boat.gridPos.adjustedRow - roadPositions(i+1).row, 2))
              dirs foreach { dir: Direction => dir match {
                case East | West => boat.move(boat.gridPos, dir, dt, hSpeed)
                case South | North => boat.move(boat.gridPos, dir, dt, vSpeed)
                case Undefined => ()
              }}
            } else if (dirs.length == 1) {
              boat.move(boat.gridPos, dirs(0), dt, boat.speed.value)
            }
          } else {
            if (boat.stabilize(boat.gridPos, dt, boat.speed.value))
              i += 1
          }
        }

        case _ => ()
      }

      if (!active) {
        destination.addVehicle(veh)
        veh.location = destination
        veh.landing()
        if (repeated) {
          val tmp: Structure = origin
          origin = destination
          destination = tmp
          origin.removeVehicle(veh)
          i = 0
          roadPositions = roadPositions.reverse
          veh.boarding(ListBuffer[Structure](destination))
        }
      }
    }
  }
}
