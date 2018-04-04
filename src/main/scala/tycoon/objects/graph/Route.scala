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



class Route(private var itinerary: ListBuffer[Road], private var stops: ListBuffer[Structure], train: Train, val repeated: Boolean) {

  def active: Boolean = currentRoadIndex <= itinerary.length - 1

  private var currentRoadIndex: Int = 0
  private var currentStopIndex: Int = 0
  private var currentStruct: Structure = stops(0)

  private var dirIndicator: Int = 0

  def start() = {
    departure()
  }

  def departure() = {
    if (currentStruct == stops(currentStopIndex)) { // train stopped here
      val nextStops: ListBuffer[Structure] = stops.drop(currentStopIndex + 1).filter(_ != currentStruct)
      train.boarding(nextStops)
      stops(currentStopIndex).trainList -= train
    }

    // dirIndicator -- 0: startStructure -> endStructure ; 1: endStructure -> startStructure
    if (currentStruct == itinerary(currentRoadIndex).startStructure.get) dirIndicator = 0
    else dirIndicator = 1

    var firstRail: Rail =
      itinerary(currentRoadIndex).rails
      .filter { rail: Rail => rail.nextInDir((dirIndicator + 1) % 2) == rail }
      .last

    train.departure(firstRail)
  }

  def arrival() = {
    train.arrival()

    if (dirIndicator == 0) currentStruct = itinerary(currentRoadIndex).endStructure.get
    else currentStruct = itinerary(currentRoadIndex).startStructure.get

    currentRoadIndex += 1

    if (currentStruct == stops(currentStopIndex + 1)) { // train stops here
      currentStopIndex += 1
      train.location = currentStruct
      train.location.trainList += train
      train.gridPos = train.location.gridPos.right
      train.landing()
    }

    if (currentStopIndex < stops.length - 1)
      departure()
  }

  def update (dt: Double) {
    if (active) {
      if (train.arrived)
        arrival()
      else
        train.update(dt, dirIndicator)
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
