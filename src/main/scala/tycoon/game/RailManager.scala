package tycoon.game

import tycoon.objects.graph._
import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.ui._

import scalafx.collections.ObservableBuffer._
import scalafx.collections.ObservableBuffer
import scala.collection.mutable.{HashMap, ListBuffer}
import scalafx.beans.property.StringProperty

class RailManager(map: Map, tilemap: TileMap, gameGraph: Graph) {

  private val rails: ListBuffer[Rail] = new ListBuffer()
  var nbNeighborRails: Int = 0

  def createRail(pos: GridLocation) : Boolean = {
    val rail = new Rail(pos)
    var created = false
    if (tilemap.gridContains(rail.gridRect) && map.isUnused(rail.gridRect)) {
      nbNeighborRails = 0

      val neighbors = Array (
        new GridLocation(pos.col, pos.row + 1),
        new GridLocation(pos.col, pos.row - 1),
        new GridLocation(pos.col + 1, pos.row),
        new GridLocation(pos.col - 1, pos.row)
      )

      for (neighbor <- neighbors) {
        map.getContentAt(neighbor) match {
          case Some(e) =>
            if (!rail.road.finished)
              created |= lookAround(rail, e)
          case None => ()
        }
      }

      if (created) {
        rails += rail
        tilemap.addEntity(rail)
        map.add(rail.gridRect, rail)

        
      }
    }
    created
  }

  def lookAround(rail: Rail, neighbor: Any): Boolean =  {
    neighbor match {
      case (s: Structure) => {
        if (rail.road.startStructure == None) {
          rail.road.startStructure = Some(s)
          nbNeighborRails += 1
          true
        }
        else if (rail.road.startStructure != Some(s)) {
          rail.road = rail.previous.road
          if (rail != rail.previous) {
            rail.road.rails += rail
            rail.road.length += 1
          }
          rail.road.endStructure = Some(s)
          rail.road.finished = true
          rail.tile = Tile.turningRailBR
          gameGraph.newRoad(rail.road)
          println("tycoon > game > RailManager.scala > lookAround > new road of " + rail.road.length + " tracks")
          true
        }
        else false
      }

      case (previousRail: Rail) => {
        if (previousRail.road_head && !previousRail.road.finished) {
          nbNeighborRails += 1

          if (nbNeighborRails == 1) {
            rail.road = previousRail.road
            rail.road.rails += rail
            rail.road.length += 1
            rail.previous = previousRail
            previousRail.next = rail
            previousRail.road_head = false
            rail.road_head = true
          }

          // merge two sides of rails
          else if (previousRail.road.startStructure != rail.road.startStructure) {
            for (r <- previousRail.road.rails) {
              val tmp = r.next
              r.next = r.previous
              r.previous = tmp
            }
            rail.next = previousRail
            previousRail.previous = rail
            rail.road.finished = true
            rail.road.endStructure = previousRail.road.startStructure
            rail.road.rails ++= previousRail.road.rails
            rail.road.length += previousRail.road.length
            previousRail.road = rail.road
            gameGraph.newRoad(rail.road)
            println("tycoon > game > RailManager.scala > lookAround > new road of " + rail.road.length + " tracks")
          }
          true
        }
        else false
      }
      case _ => false
    }
  }
}
