package tycoon.game

import scala.collection.mutable.ListBuffer
import scala.Array._

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.ui.Tile

object Dijkstra {
  def tileGraph(struc1 : Structure ,struc2 : Structure ,authorizedTile : Array[Tile],map : TileMap) : Boolean = {
    type Route = ListBuffer[GridLocation]
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
    def getSurroundingPos(pos : GridLocation) : Array [GridLocation] = {
      Array((pos.top),
      (pos.right),
      (pos.bottom),
      (pos.left))
    }
    //position actuelle
    var position : GridLocation = struct1.gridPos
    //matrice des ddistances
    var distanceTable = Array.fill[Option[Int],Route](map.width,map.height)(None,[])
    var easyList = ListBuffer[GridLocation,Int](position)
    var valid = true
    //On parcoure tant que des élément sont non visité
    while (valid){
      //On cherche un nouveau point à partir de la pos
      /// ATTIENTION il faut choisir le plus petit chemin jusqu'a (prendre dans  listbuffer c'est plus facile)!

      for ( newPos <- getSurroundingPos(position)) {
        if (map.checkBgTiles(newPos,authorizedTile)){
          position = newPos
        }
      }
    }



    true
  }
}
