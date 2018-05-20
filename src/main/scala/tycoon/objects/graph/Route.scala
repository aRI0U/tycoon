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



class Route(private var itinerary: ListBuffer[Road], var stops: ListBuffer[Structure], val vehicle: Vehicle, var repeated: Boolean) {

  def active: Boolean = currentRoadIndex <= itinerary.length - 1

  private var currentRoadIndex: Int = 0
  private var currentStopIndex: Int = 0
  private var currentStruct: Structure = stops(0)

  private var dirIndicator: Int = 0 // for trains only
  private var currentBreakTime: Double = 0.0

  def start() = {
    departure()
  }

  def departure() = {
    var stopping: Boolean = false
    if (currentStruct == stops(currentStopIndex)) { // vehicle stopped here
      val nextStops: ListBuffer[Structure] = stops.drop(currentStopIndex + 1).filter(_ != currentStruct)
      vehicle.boarding(nextStops)
      stops(currentStopIndex).removeVehicle(vehicle)
    }

    // dirIndicator -- 0: startStructure -> endStructure ; 1: endStructure -> startStructure
    if (currentStruct == itinerary(currentRoadIndex).startStructure.get) dirIndicator = 0
    else dirIndicator = 1

    vehicle match {
      case train: Train => {
        var firstRail: Rail =
          itinerary(currentRoadIndex).rails
          .filter { rail: Rail => rail.nextInDir((dirIndicator + 1) % 2) == rail }
          .last
        train.departure(firstRail)
      }
      case _ => vehicle.departure()
    }

    currentBreakTime = 0.0
  }

  def arrival() = {
    vehicle.arrival()

    if (dirIndicator == 0) currentStruct = itinerary(currentRoadIndex).endStructure.get
    else currentStruct = itinerary(currentRoadIndex).startStructure.get

    currentRoadIndex += 1

    if (currentStruct == stops(currentStopIndex + 1)) { // vehicle stops here
      currentStopIndex += 1
      vehicle.location = currentStruct
      vehicle.location.addVehicle(vehicle)

      vehicle match {
        case train: Train => train.gridPos = train.location.gridPos.right
        case _ => vehicle.gridPos = vehicle.location.gridPos
      }

      vehicle.landing()
    }


    //if (currentStopIndex < stops.length - 1)
      // departure()
    currentBreakTime = 2.0
  }

  def update (dt: Double) {
    if (currentBreakTime > 0)
      currentBreakTime -= dt
    else if (active) {
      if (vehicle.arrived) {
        if (currentBreakTime == 0) arrival()
        else if (currentBreakTime < 0) departure()
      }
      else
        vehicle.update(dt, dirIndicator)
    }
    else if (repeated) {
      stops = stops.reverse
      itinerary = itinerary.reverse
      currentRoadIndex = 0
      currentStopIndex = 0
      currentStruct = stops(0)
      departure()
    }
  }
}
