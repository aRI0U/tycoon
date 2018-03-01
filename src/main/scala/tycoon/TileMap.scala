package tycoon


import tycoon.ui.Tile
import scala.collection.mutable.HashMap
import scalafx.scene.image.Image

import scalafx.geometry.Rectangle2D
import  scala.util.Random

class TileMap {
  val map = HashMap.empty[GridLocation, Tile]

  private var _col_min : Int = 0
  private var _col_max : Int = 0
  private var _row_min : Int = 0
  private var _row_max : Int = 0

  private var _tile_width : Int = 0
  private var _tile_height : Int = 0

  def col_min : Int = _col_min
  def col_max : Int = _col_max
  def row_min : Int = _row_min
  def row_max : Int = _row_max

  def tile_width : Int = _tile_width
  def tile_height : Int = _tile_height

  def gridRect : Rectangle2D = new Rectangle2D(col_min , row_min , col_max - col_min + 1, row_max - row_min + 1)

  def setSize(width: Int, height: Int) : Unit = {
    _col_min = - width / 2
    _col_max  = _col_min + width - 1
    _row_min  = - height / 2
    _row_max = _row_min + height - 1
  }

  def fill(tile_array : Array[Tile]) : Unit = {
    map.clear()
    _tile_width = tile_array(1).width
    _tile_height = tile_array(1).height
    var r = scala.util.Random
    var i = 0
    for {
      col <- col_min to col_max
      row <- row_min to row_max
    } map += (new GridLocation(col, row) -> new Tile(tile_array(scala.util.Random.nextInt(3))))

}
  def fillBorder(tile: Tile, borderSize: Int, shift: Int = 0) : Unit = {
    for {
      col <- (col_min - borderSize - shift) to (col_max + borderSize + shift)
      row <- (row_min - borderSize - shift) to (row_max + borderSize + shift)
      if (col < col_min - shift || col > col_max + shift
        || row < row_min - shift || row > row_max + shift)
    } map += (new GridLocation(col, row) -> new Tile(tile))
  }
}
