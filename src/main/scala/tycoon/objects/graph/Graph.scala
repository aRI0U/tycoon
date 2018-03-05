package tycoon.objects.graph

import scala.collection.mutable.ListBuffer
import scala.Array._

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.Game

class Vertex(s: Structure) {
  val origin : Int = s.structure_id
  var links : ListBuffer[(Int,Road)] = new ListBuffer
}

// Each game has an abstract graph, whose nodes are structures and edges are roads. This graph is used to compute the shortest routes from different towns.

class Graph {
  var content : ListBuffer[Vertex] = new ListBuffer

  def newStructure(s:Structure) : Unit = {
    content += new Vertex(s)
    println("added structure n° " + s.structure_id)
  }

  def newRoad(road:Road) : Unit = {
    if (road.finished) {
      road.start_town match {
        case None => println("unfinished road?")
        case Some(s_town) => road.end_town match {
          case None => println("unfinished road?")
          case Some(e_town) => {
            val s_id = s_town.structure_id
            val e_id = e_town.structure_id
            for (vertex <- content) {
              vertex.origin match {
                case s_id => vertex.links += ((e_id, road))
                case e_id => vertex.links += ((s_id, road))
                case _ => ;
              }
            }
            println("added road from " + s_id + " to " + e_id)
          }
        }
      }
    }
  }

  // returns true iff m < n (None means infinity)
  def optionMin(m: Option[Int], n: Option[Int]) : Boolean = {
    m match {
      case None => false
      case Some(x) => n match {
        case None => true
        case Some(y) => (x < y)
      }
    }
  }

  // returns m+n iff m and n are both integers, and infinity (None) otherwise
  def optionSum(m: Option[Int], n: Option[Int]) : Option[Int] = {
    m match {
      case None => None
      case Some(x) => n match {
        case None => None
        case Some(y) => Some(x+y)
      }
    }
  }

  def shortestRoute(departure: Structure, arrival: Structure) : ListBuffer[Road] = {
    // initialization
    var l = content.length
    var d : Array[Option[Int]] = new Array[Option[Int]](l)
    for (i <- 0 to l-1) d(i) = None
    d(departure.structure_id) = Some(0)

    var not_visited : ListBuffer[Vertex] = content.clone
    var previous : Array[Option[(Int, Road)]] = new Array[Option[(Int, Road)]](l)
    for (i <- 0 to l-1) previous(i) = None

    while (!(not_visited.isEmpty)) {
      // find closest structure
      var mini : Option[Int] = None
      var next_stop : Vertex = not_visited(0)
      for (s <- not_visited) {
        val distance : Option[Int] = d(s.origin)
        if (optionMin(distance, mini)) {
          mini = distance
          next_stop = s
        }
      }
      not_visited -= next_stop

      for (neighbor <- next_stop.links) {
        val id = neighbor._1
        val road = neighbor._2
        val new_path = optionSum(d(next_stop.origin), Some(road.length))
        if (optionMin(new_path, d(id))) {
          d(id) = new_path
          previous(id) = Some((next_stop.origin, road))
        }
      }
    }
    var final_path = new ListBuffer[Road]
    var last_step = arrival.structure_id
    while (last_step != departure.structure_id) {
      var predecessor = previous(last_step)
      predecessor match {
        case Some(p) => {
          final_path += p._2
          last_step = p._1
        }
        case _ => println ("what's going on...")
      }
    }
    final_path
  }
}
