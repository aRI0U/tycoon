package tycoon.game

import scala.collection.mutable.ListBuffer
import scala.Array._
import scala.collection.mutable.PriorityQueue

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.ui.Tile
import scala.collection.mutable.ListBuffer
import scala.math
import scala.collection.mutable.Set

object Dijkstra {
  def tileGraph(struct1 : Structure ,struct2 : Structure ,authorizedTile : Array[Tile],map : TileMap) : ListBuffer[GridLocation] = {

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
          case Some(y) => Some(x + y)
        }
      }
    }
    def isFacility(pos : GridLocation) : Boolean = {
      map.maybeGetStructureAt(pos) match {
        case Some(a : Airport) => true
        case Some(d : Dock) => true
        case Some(r : Rail) => true
        case Some(f : Factory) => true
        case Some(f : Farm) => true
        case Some(f : Field) => true
        case _ => false
      }
    }
    def isStruct(pos : GridLocation) : Boolean = {
      map.maybeGetStructureAt(pos) match {
        case Some(s) => (s == struct1 || s == struct2)
        case _ => false
      }
    }

    def getSurroundingPos(pos : GridLocation) : ListBuffer[GridLocation] = {
      if (pos.col > 0 && pos.row > 0 && pos.row< map.height -2 && pos.col < map.width -2) {
        var neighbors = ListBuffer[GridLocation]()
        var potential = ListBuffer[GridLocation](pos.top,pos.right,pos.bottom,pos.left)
        for (po <- potential) {
          if (map.checkBgTile(po,authorizedTile) || isStruct(po)) {
            neighbors += po
          }
        }
        neighbors
      }
      else ListBuffer[GridLocation]()
    }

    var lastPos = struct2.gridPos
    var initPos = struct1.gridPos

    def diff(loc : GridLocation) = Math.abs(lastPos.col-loc.col) + Math.abs(lastPos.row-loc.row)//(Math.sqrt(Math.pow((lastPos.col-loc.col),2) + Math.pow(lastPos.row-loc.row,2)))

    var distanceTable = Array.fill[Option[Int]](map.width, map.height)(None)
    var heuristicTable : Array[Array[Int]] = Array.fill[Int](map.width, map.height)(0)
    //unifie
    for ((col, row) <- new GridRectangle(0, 0, map.width, map.height).iterateTuple) {
      heuristicTable(col)(row) = diff(new GridLocation(col,row)).toInt + 2000
    }
    var notVisited : ListBuffer[GridLocation] = new ListBuffer[GridLocation]
    for ((col, row) <- new GridRectangle(0, 0, map.width, map.height).iterateTuple) {
      if ((map.checkBgTile(new GridLocation(col, row),authorizedTile) && !isFacility(new GridLocation(col, row))) || isStruct(new GridLocation(col, row))) {
        notVisited += new GridLocation(col, row)
      }
    }
    heuristicTable(initPos.col)(initPos.row) = diff(new GridLocation(initPos.col,initPos.row)).toInt
    distanceTable(initPos.col)(initPos.row) = Some(0)
    notVisited -= initPos
    var previous : Array[Array[Option[GridLocation]]]= Array.fill[Option[GridLocation]](map.width, map.height)(None)

    for (pos <- getSurroundingPos(initPos)) {
      heuristicTable(pos.col)(pos.row) = diff(pos).toInt + 1
      distanceTable(pos.col)(pos.row) = Some(1)
      previous(pos.col)(pos.row) = Some(initPos)
    }

    while (notVisited.nonEmpty) {
      var mini : Int = Int.MaxValue
      var nextPos : GridLocation = notVisited(0)
      for (pos <- notVisited) {
        val heuristic = heuristicTable(pos.col)(pos.row)
        if (heuristic < mini) {
          mini = heuristic
          nextPos = pos
        }
      }
      notVisited -= nextPos

      if (nextPos.col == lastPos.col && nextPos.row == lastPos.row) {
        notVisited = ListBuffer[GridLocation]()
      }

      for (neighbor <- getSurroundingPos(nextPos)) {
        val newDistance = optionSum(distanceTable(nextPos.col)(nextPos.row),Some(1))
        if (optionMin(newDistance, distanceTable(neighbor.col)(neighbor.row))) {
          heuristicTable(neighbor.col)(neighbor.row) = diff(neighbor).toInt + newDistance.get
          distanceTable(neighbor.col)(neighbor.row) = newDistance
          previous(neighbor.col)(neighbor.row) = Some(nextPos)
        }
      }
    }
    var finalPath = ListBuffer[GridLocation](lastPos)
    var validity = true
    while (lastPos != initPos) {
      var predecessor = previous(lastPos.col)(lastPos.row)
      predecessor match {
        case Some(p) => {
          finalPath+=p
          lastPos = p
        }
        case None => {
          validity = false
          lastPos = initPos
        }
      }
    }
    println("Tycoon > objects > graph > Dijkstra > validity of the path:", validity)
    if (validity) finalPath.reverse
    else new ListBuffer[GridLocation]()
  }
}
