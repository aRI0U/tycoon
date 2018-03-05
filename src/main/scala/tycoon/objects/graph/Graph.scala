package tycoon.objects.graph

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._
import tycoon.objects.railway._

class Vertex(s: Structure) {
  val origin : Int = s.structure_id
  var links : ListBuffer[(Int,Int)] = new ListBuffer
}


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
                case s_id => vertex.links += ((e_id, road.length))
                case e_id => vertex.links += ((s_id, road.length))
                case _ => ;
              }
            }
            println("added road from " + s_id + " to " + e_id)
          }
        }
      }
    }
  }
}
