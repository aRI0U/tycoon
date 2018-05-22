package tycoon.game

import scala.collection.mutable.ListBuffer
import scala.collection.immutable.List

import tycoon.objects.good.Good
import tycoon.objects.graph._
import tycoon.objects.railway._
import tycoon.objects.structure._

import scalafx.beans.property.{IntegerProperty, StringProperty}


class TownManager(game: Game) {

  // names
  var townNames = new ListBuffer[String]
  townNames += ("Paris", "Lyon", "Toulouse", "Saclay", "Nice", "Strasbourg", "Mulhouse", "Aulnay-sous-Bois", "Cachan", "Hamburg", "Berlin", "Brno", "Caderousse","Stuttgart", "Wien", "Koln")

  // information about towns
  var townsList = new ListBuffer[Town]
  var structuresList = new ListBuffer[Structure]

  def newStructure(structure: Structure) {
    for (t <- townsList) {
      // add the new structure to potential destinations
      if (structure != t)
        t.destinations += structure

      t.waitersInt += IntegerProperty(0)
    }

    // add to every agent the new structure's weighting and to the new structure every agent's weighting
    structure match {
      case agent: EconomicAgent => {
        agent.newWeighting(agent)
        for (s <- structuresList) {
          s match {
            case a: EconomicAgent => {
              agent.newWeighting(a)
              a.newWeighting(agent)
            }
            case _ => ()
          }
        }
      }
      case _ => ()
    }
    structuresList += structure
    structure.owner.get(structure)
  }

  def newTown(town: Town) {
    newStructure(town)
    for (t <- townsList) t.printData(2).newRankedElement(town.name, t.waitersInt.last)
    town.displayWaiters()
    townsList += town
    town.owner.towns += town
  }


  // DETERMINE DISTANCES

  // economy of the different cities take into account the economy of cities around it

  /// to determine distances by train, we use the graph of rails and stations generated previously

  def determineVertex(index: Int, graph: Graph) : Option[Vertex] = {
    var i = 0
    while (i < graph.content.length && graph.content(i).origin != index) i += 1
    if (i < graph.content.length) Some(graph.content(i))
    else None
  }

  def explore(vertex: Vertex, graph: Graph, notVisited: ListBuffer[Vertex], stack: List[Road], arrival: Vertex) : Option[Int] = {
    if (vertex == arrival) {
      var distance = 0
      var stackCopy = stack
      while (!stackCopy.isEmpty) {
        distance += stackCopy.head.length
        stackCopy = stackCopy.tail
      }
      Some(distance)
    }
    else {
      notVisited -= vertex
      var optDistance : Option[Int] = None
      for (link <- vertex.links) {
        determineVertex(link._1, graph) match {
          case Some(v) => {
            val i = notVisited.indexOf(determineVertex(link._1, graph).get)
            if (i != -1) {
              determineVertex(link._1, graph) match {
                case Some(v) => optDistance = graph.optionMin(optDistance, explore(v, graph, notVisited, link._2 :: stack, arrival))
                case None => ()
              }
            }
          }
          case None => ()
        }
      }
      optDistance
    }
  }

  def determineRailwayDistance(s1: Structure, s2: Structure) : Option[Int] = {
    // DFS
    var stack : List[Road] = List()
    val graph = game.gameGraph
    var notVisited = graph.content.clone()
    var distance : Option[Int] = None
    determineVertex(s1.structureId, graph) match {
      case Some(vertex) => {
        determineVertex(s2.structureId, graph) match {
          case Some(arrival) => explore(vertex, graph, notVisited, stack, arrival)
          case None => None
        }
      }
      case None => None
    }
  }

  def determineEuclidianDistance(s1: Structure, s2: Structure) : Int =
    Math.sqrt((Math.pow(s1.gridPos.col - s2.gridPos.col, 2) + Math.pow(s1.gridPos.row - s2.gridPos.row, 2))).toInt

  def getTime() : Double = game.totalElapsedTime

  def throwEvent(s: String) = game.setInfoText(s, 3)

  def updateWeightings(s: EconomicAgent) = s.updateWeightings()

  def updatePortWeightings(i: Int) = {
    for (s <- structuresList) {
      s match {
        case t: Town => if (i == 0 && t.hasAirport || i == 1 && t.hasDock) t.updateWeightings()
        case _ => ()
      }
    }
  }
}
