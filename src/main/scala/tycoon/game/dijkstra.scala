package tycoon.game

import scala.collection.mutable.ListBuffer
import scala.Array._

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.ui.Tile
import scala.collection.mutable.ListBuffer
import scala.math
import scala.collection.mutable.Set

object Dijkstra {
  def tileGraph(struct1 : Structure ,struct2 : Structure ,authorizedTile : Array[Tile],map : TileMap) : ListBuffer[GridLocation] = {
    // type Track = ListBuffer[GridLocation]
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
    def getSurroundingPos(pos : GridLocation) : ListBuffer[GridLocation] = {
      if (pos.col > 0 && pos.row > 0 && pos.row< map.height -2 && pos.col < map.width -2) {
        var neighbors = ListBuffer[GridLocation]()
        var potential = ListBuffer[GridLocation](pos.top,pos.right,pos.bottom,pos.left)
        for (po <- potential) {
          if (map.checkBgTile(po,authorizedTile)) {
            neighbors += po
          }
        }
        neighbors
      }
      else ListBuffer[GridLocation]()
    }

    var initPos = struct1.gridPos
    var distanceTable : Array[Array[Option[Int]]] = Array.fill[Option[Int]](map.width, map.height)(None) //Array.fill[Array[Option[Int]]](map.height)(d)
    distanceTable(initPos.col)(initPos.row) = Some(0)
    var notVisited : ListBuffer[GridLocation] = new ListBuffer[GridLocation]
    for ((col, row) <- new GridRectangle(0, 0, map.width, map.height).iterateTuple)
      if (map.checkBgTile(new GridLocation(col, row),authorizedTile)) {
        notVisited += new GridLocation(col, row)
      }

    notVisited -= initPos
    var previous : Array[Array[Option[GridLocation]]]= Array.fill[Option[GridLocation]](map.width, map.height)(None) //Array.fill[Array[Option[GridLocation]]](map.height)(p)

    // def sortByDistance(pos1 : GridLocation, pos2 : GridLocation) = {
    //   optionMin(distanceTable(pos2.col)(pos2.row),distanceTable(pos1.col)(pos1.row))
    // }
    // def getDistance(pos : GridLocation) = {
    //   distanceTable(pos.col)(pos.row)
    // }

    //Initialisation
    for (pos <- getSurroundingPos(initPos)) {
      distanceTable(pos.col)(pos.row) = Some(1)
      previous(pos.col)(pos.row) = Some(initPos)
      notVisited -= pos
      notVisited += pos
    }
    // notVisited.sortWith(sortByDistance)

    while (notVisited.nonEmpty) {
      var mini : Option[Int] = None
      var nextPos : GridLocation = notVisited(notVisited.size -1)
      // nextPos = notVisited.min(Ordering.by(getDistance(_ : GridLocation)))

      for (pos <- notVisited) {
        val distance : Option[Int] = distanceTable(pos.col)(pos.row)
        if (optionMin(distance, mini)) {
          // println("nouvelle position",pos)
          mini = distance
          // println("a distance",mini)
          nextPos = pos
          if (nextPos == struct2.gridPos) {
            notVisited = ListBuffer[GridLocation]()
          }
        }
      }
      notVisited -= nextPos

      for (neighbor <- getSurroundingPos(nextPos)) {
        val newPath = optionSum(distanceTable(nextPos.col)(nextPos.row), Some(1))
        if (optionMin(newPath, distanceTable(neighbor.col)(neighbor.row))) {
          distanceTable(neighbor.col)(neighbor.row) = newPath
          previous(neighbor.col)(neighbor.row) = Some(nextPos)
        }
      }
      // notVisited.sortWith(sortByDistance)
    }
    var finalPath = new ListBuffer[GridLocation]
    var lastPos = struct2.gridPos
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
    println("validity of the path",validity)
    if (validity) finalPath
    else new ListBuffer[GridLocation]()
  }
}
