package tycoon.objects.graph

import tycoon.GridLocation

import Array._

class Map(width: Int, height: Int) {
  // content(i)(j) iff there is a rail on its tile
  var content = ofDim[Boolean](height, width)
  def addToMap(pos: GridLocation, kind: Boolean) : Unit = {
    val i = pos.row + height/2
    val j = pos.col + width/2
    content(i)(j) = kind
    println("just added a rail on " + i + ", " + j)
  }
  def findPaths(departure: GridLocation, length: Int) : Unit = {
    val d_i = departure.row + height/2
    val d_j = departure.col + width/2

  }
}
