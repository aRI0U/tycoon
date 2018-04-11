package tycoon.game

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

import tycoon.objects.good.Good
import tycoon.objects.graph._
import tycoon.objects.railway._
import tycoon.objects.structure._

import scalafx.beans.property.{IntegerProperty, StringProperty}


class TownManager(game: Game) {

  // names
  var townNames = new ListBuffer[String]
  townNames += ("Paris", "Lyon", "Toulouse", "Saclay", "Nice", "Strasbourg", "Mulhouse", "Aulnay-sous-Bois", "Cachan", "Hamburg", "Berlin", "Brno", "Caderousse","Stuttgart", "Wien", "Köln")

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
    for (s <- structuresList) {
      s match {
        case t: Town => t.newWeighting(s)
        case f: Facility => f.newWeighting(s)
        case _ => ()
      }
    }
    structuresList += structure
  }

  def newTown(town: Town) {
    newStructure(town)
    for (t <- townsList) t.printData(2).newRankedElement(town.name, t.waitersInt.last)
    town.displayWaiters()
    townsList += town
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

  def explore(vertex: Vertex, graph: Graph, notVisited: ListBuffer[Vertex], stack: Stack[Road], arrival: Vertex) : Option[Int] = {
    if (vertex == arrival) {
      var distance = 0
      while (!stack.isEmpty) distance += stack.pop().length
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
                case Some(v) => optDistance = graph.optionMin(optDistance, explore(v, graph, notVisited, stack.push(link._2), arrival))
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
    var stack = new Stack[Road]
    val graph = game.game_graph
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


  // def debugDistances() = {
  //   for (s <- structuresList) {
  //     for (s1 <- structuresList) {
  //       println("Distance between "+s.structureId+" and "+s1.structureId)
  //       println(determineRailwayDistance(s,s1))
  //     }
  //   }
  // }

  def getTime() : Double = game.totalElapsedTime

  def throwEvent(s: String) = game.setInfoText(s, 3)


  // ECONOMY

  val economicGoods = new ListBuffer[EconomicGood]

  def getEconomicGood(good: Good) : EconomicGood = {
    var i = 0
    while (i < economicGoods.length && economicGoods(i).kind != good) i += 1
    if (i == economicGoods.length) {
      economicGoods += new EconomicGood(good)
    }
    economicGoods(i)
  }

  def getReport(s: Structure, kind: Good, stocks: IntegerProperty, requests: IntegerProperty) = {
    val good = getEconomicGood(kind)
    good.newEmergence(s, stocks, requests)
    s match {
      case t: Town => {
        println("debug > town matched")
        t.newEconomicGood(good)
        println("debug > ok")
      }
      case f: Facility => f.newEconomicGood(good)
      case _ => ()
    }
  }
}
