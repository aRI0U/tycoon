package tycoon.game

import tycoon.ui.Tile
import tycoon.ui.Renderable
import scala.util.Random
import scalafx.geometry.Rectangle2D
import scala.collection.mutable.ListBuffer
import scala.math


/*
layer -1 (backgroundLayer) : background (grass, trees, rocks, lakes..)
layer 0 : structures (town, airport, farm, factory, mine..) and rails
layer 1 : movable entities (trains, planes, boats..)

*/
class TileMap (val width: Int, val height: Int, nbEntityLayers: Int = 2) {
  // general map
  def gridContains(rect: GridRectangle) =
    (rect.left >= 0 && rect.top >= 0 && rect.right <= width - 1 && rect.bottom <= height - 1)


  // grid of structural entities (ie towns but not trains, max one per case (Option type))
  private var content = Array.fill[Option[Renderable]](width, height)(None)

  // entities layers (for each layer, list of every entity on this layer)
  private val entityLayers: Array[ListBuffer[Renderable]] = new Array(nbEntityLayers)
  for (i <- 0 to nbEntityLayers - 1)
    entityLayers(i) = new ListBuffer[Renderable]


  def add(e: Renderable, layer: Int = 0) = {
    if (layer == 0)
      for ((col, row) <- e.gridRect.iterate)
        content(col)(row) = Some(e)
    entityLayers(layer) += e
  }

/*
  def remove(rect: GridRectangle) = {
    for ((col, row) <- rect.iterate) {
      content(col)(row) = None
      println("tycoon > objects > graph > Map.scala > addToMap: added element at pos (" + col + ", " + row + ")")
    }
  } */

  ///def removeEntity(e: Renderable, layer: Int = 0) = entityLayers(layer) -= e

  def isUnused(pos: GridLocation): Boolean =
    content(pos.col)(pos.row) == None

  def isUnused(rect: GridRectangle): Boolean = {
    var bool = true
    for ((col, row) <- rect.iterate) {
      if (content(col)(row) != None)
        bool = false
    }
    bool
  }

  def getContentAt(pos: GridLocation): Option[Renderable] = content(pos.col)(pos.row)
  def getEntities: Array[ListBuffer[Renderable]] = entityLayers.clone()


  // background layer
  private val backgroundLayer = Array.fill[Tile](width, height)(Tile.default)

  def getBackgroundTile(col: Int, row: Int) = backgroundLayer(col)(row)
  def addBackgroundTile(col: Int, row: Int, tile: Tile) = backgroundLayer(col)(row) = tile

  def checkBgTile(pos: GridLocation, tile: Tile) : Boolean = backgroundLayer(pos.col)(pos.row) == tile
  // convenience overloads of checkBgTile
  def checkBgTile(pos: GridLocation, tiles: Array[Tile]) : Boolean = tiles.exists(checkBgTile(pos, _))
  def checkBgTile(col: Int, row: Int, tile: Tile) : Boolean = checkBgTile(new GridLocation(col, row), tile)
  def checkBgTile(col: Int, row: Int, tiles: Array[Tile]) : Boolean = tiles.exists(checkBgTile(col, row, _))

  /* randomly fill background layer of map using tiles */
  def fillBackground(tiles: Array[Tile]) : Unit = {
    val r = scala.util.Random
    if (tiles.length >= 1)
      for ((col, row) <- new GridRectangle(0, 0, width, height).iterate)
        backgroundLayer(col)(row) = tiles(r.nextInt(tiles.length))
  }

  /* randomly add tile in background layer given a rate of apperance */
  def sprinkleTile(tile: Tile, pourcentage: Int) = {
    val r = scala.util.Random
    if (pourcentage >= 1 && pourcentage <= 100)
      for ((col, row) <- new GridRectangle(0, 0, width, height).iterate)
        if (r.nextInt(100) + 1 <= pourcentage && checkBgTile(col, row, Tile.grass))
          backgroundLayer(col)(row) = tile
  }

  // 2 ratios of points that will become lakes and points in teselation
  /* generate lakes idk how */
  def generateLakes(choosenPoint : Int, generatedPoints : Int) : Unit = {
    val r = scala.util.Random

    // add random points in lakeStarter at a rate of 1 / generatedPoints
    var lakeStarter = new ListBuffer[GridLocation]
    for ((col, row) <- new GridRectangle(0, 0, width, height).iterate)
      if (r.nextInt(generatedPoints) == 0)
        lakeStarter += new GridLocation(col, row)

    var teselationPoints : Array[ListBuffer[GridLocation]] = new Array(lakeStarter.size)
    // var lakeCenters = new ListBuffer[Int]
    for (i <- 0 to lakeStarter.size - 1)
      teselationPoints(i) = new ListBuffer[GridLocation]

    for ((col, row) <- new GridRectangle(0, 0, width, height).iterate) {
      var distance = height + width
      var nearestPoint = 0
      var counter = 0
      for (pos <- lakeStarter) {
        var x = Math.abs(pos.row - row)
        var y = Math.abs(pos.col - col)
        var r = Math.ceil(Math.sqrt(Math.pow(x,2) + Math.pow(y,2))).toInt
        if (r < distance) {
          distance = r
          nearestPoint = counter
        }
        counter += 1
      }
      teselationPoints(nearestPoint) += new GridLocation(col, row)
    }
    for (i <- 0 to lakeStarter.size -1) {
      var randomPoint = scala.util.Random
      if (randomPoint.nextInt(choosenPoint) == 1) {
        for (pos <- teselationPoints(i)) {
          backgroundLayer(pos.col)(pos.row) = Tile.plainWater
        }
          // traiter les cas de bordure de lac pour rendre les bon tiles
        for (pos <- teselationPoints(i)) {
          if (pos.col > 0 && pos.row > 0 && pos.row< height -2 && pos.col < width -2) {
            val neighbors = Array (
              new GridLocation(pos.col, pos.row - 1),
              new GridLocation(pos.col + 1, pos.row),
              new GridLocation(pos.col, pos.row + 1),
              new GridLocation(pos.col - 1, pos.row)
            )
            for (j <- 0 to 3) {
              if (checkBgTile(neighbors(j), Tile.grass))
                backgroundLayer(pos.col)(pos.row) = Tile.plainSand
            }
          }
        }
      }
    }
  }
}
