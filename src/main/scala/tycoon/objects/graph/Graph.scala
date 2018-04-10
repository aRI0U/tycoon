package tycoon.objects.graph

import scala.collection.mutable.ListBuffer
import scala.Array._

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.game.Game

class Vertex(s: Structure) {
  val origin : Int = s.structureId
  var links : ListBuffer[(Int,Road)] = new ListBuffer
}

// Each game has an abstract graph, whose nodes are structures and edges are roads. This graph is used to compute the shortest routes from different towns.

class Graph {
  var content : ListBuffer[Vertex] = new ListBuffer

  def newStructure(s:Structure) : Unit = {
    content += new Vertex(s)
    println("tycoon > objects > graph > Graph.scala > newStructure: added structure n° " + s.structureId)
  }

  def newRoad(road:Road) : Unit = {
    if (road.finished) {
      road.startStructure match {
        case None => ()
        case Some(s_town) => road.endStructure match {
          case None => ()
          case Some(e_town) => {
            val s_id = s_town.structureId
            val e_id = e_town.structureId
            for (vertex <- content) {
              if (vertex.origin == s_id) vertex.links += ((e_id, road))
              else {if (vertex.origin == e_id) vertex.links += ((s_id, road))}
            }
            println("tycoon > objects > graph > Graph.scala > newRoad: added road from " + s_id + " to " + e_id)
          }
        }
      }
    }
    print_graph()
  }

  def removeStructure(s: Structure) = {
    val id = s.structureId
    for (vertex <- content) {
      if (vertex.origin == id) content -= vertex
      else {
        for (linked_station <- vertex.links) {
          if (linked_station._1 == id) vertex.links -= linked_station
        }
      }
    }
  }

  def print_graph() {
    for (vertex <- content) {
      println("tycoon > objects > graph > Graph.scala > print_graph: Vertex " + vertex.origin + ":")
      for (link <- vertex.links) {
        println("tycoon > objects > graph > Graph.scala > print_graph: |--> connected to " + link._1)
      }
    }
  }

  // returns true iff m < n (None means infinity)
  def optionInf(m: Option[Int], n: Option[Int]) : Boolean = {
    m match {
      case None => false
      case Some(x) => n match {
        case None => true
        case Some(y) => (x < y)
      }
    }
  }

  def optionMin(m: Option[Int], n: Option[Int]) : Option[Int] = {
    m match {
      case None => n
      case Some(x) => n match {
        case None => m
        case Some(y) => Some(x.min(y))
      }
    }
  }

  // returns m+n iff m and n are both integers, and infinity (None) otherwise
  def optionSum(m: Option[Int], n: Option[Int]) : Option[Int] = {
    m match {
      case None => None
      case Some(x) => n match {
        case None => None
        case Some(y) => Some(x + y)
      }
    }
  }

  def shortestRoute(departure: Structure, arrival: Structure) : ListBuffer[Road] = {
    val l = content.length

    var d : Array[Option[Int]] = new Array[Option[Int]](l)
    for (i <- 0 to l-1) d(i) = None
    d(departure.structureId) = Some(0)

    var notVisited : ListBuffer[Vertex] = content.clone
    var previous : Array[Option[(Int, Road)]] = new Array[Option[(Int, Road)]](l)
    for (i <- 0 to l-1) previous(i) = None

    while (notVisited.nonEmpty) {
      // find closest structure
      var mini : Option[Int] = None
      var next_stop : Vertex = notVisited(0)
      for (s <- notVisited) {
        val distance : Option[Int] = d(s.origin)
        if (optionInf(distance, mini)) {
          mini = distance
          next_stop = s
        }
      }
      notVisited -= next_stop

      for (neighbor <- next_stop.links) {
        val id = neighbor._1
        val road = neighbor._2
        val new_path = optionSum(d(next_stop.origin), Some(road.length))
        if (optionInf(new_path, d(id))) {
          d(id) = new_path
          previous(id) = Some((next_stop.origin, road))
        }
      }
    }
    var final_path = new ListBuffer[Road]
    var last_step = arrival.structureId
    var k = l
    while (last_step != departure.structureId) {
      var predecessor = previous(last_step)
      predecessor match {
        case Some(p) => {
          final_path.prepend(p._2) // preprend
          last_step = p._1
        }
        case None => throw new IllegalStateException("no path joining the two structures")
      }
    }
    final_path
  }
}
