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



class Route(itinerary: ListBuffer[Road], stops: ListBuffer[Structure], train: Train) {

  def active: Boolean = currentRoadIndex <= itinerary.length - 1

  private var currentRoadIndex: Int = 0
  private var currentStopIndex: Int = 0
  private var currentStruct: Structure = stops(0)

  private var dirIndicator: Int = 0

  def start() = {
    departure()
  }

  def departure() = {
    println("departure")
    // train stopped at this stop
    if (currentStruct == stops(currentStopIndex)) {
      println("boards here")
      val nextStops: ListBuffer[Structure] = stops.drop(currentStopIndex + 1).filter(_ != currentStruct)
      train.boarding(nextStops)

      stops(currentStopIndex).trainList -= train
      train.visible = true
      train.carriageList foreach (_.visible = true)
    }

    // dirIndicator -- 0: startStructure -> endStructure ; 1: endStructure -> startStructure
    if (currentStruct == itinerary(currentRoadIndex).startStructure.get) dirIndicator = 0
    else dirIndicator = 1

    for (rail <- itinerary(currentRoadIndex).rails)
      if (rail.nextInDir((dirIndicator + 1) % 2) == rail) {
        train.currentRail = Some(rail)
        train.gridPos = rail.gridPos.clone()
      }

    train.gridPos = (train.currentRail.get).gridPos.clone()
  }

  def arrival() = {
    println("arrival")

    if (dirIndicator == 0) currentStruct = itinerary(currentRoadIndex).endStructure.get
    else currentStruct = itinerary(currentRoadIndex).startStructure.get

    currentRoadIndex += 1

    if (currentStruct == stops(currentStopIndex + 1)) { // stop at this struct
      println("lands here")
      currentStopIndex += 1
      train.location = currentStruct
      train.location.trainList += train
      train.gridPos = train.location.gridPos.right
      train.landing()
    } else {
      train.arrived = false
    }

    if (currentStopIndex == stops.length - 1) { // arrival
      for (carr <- train.carriageList) {
        carr.visible = false
        carr.currentRail = None
      }
    }
    else {
      departure()
    }
  }

  def update (dt: Double) {
    if (active) {
      if (train.arrived)
        arrival()
      else
        train.move(dt, dirIndicator)
    }
  }
}
