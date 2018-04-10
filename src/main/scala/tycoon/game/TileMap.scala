package tycoon.game

import tycoon.ui.Tile
import tycoon.ui.Renderable
import scala.util.Random
import scalafx.geometry.Rectangle2D
import scala.collection.mutable.ListBuffer
import scala.math
import scala.collection.mutable.Set


class TileMap (val width: Int, val height: Int) {
  private val backgroundLayer = Array.fill[Tile](width, height)(Tile.Default)
  private var structuresLayer = Array.fill[Option[Renderable]](width, height)(None)
  private val entityLayer = ListBuffer[Renderable]()

  /** test whether pos/rect is included in map */
  def gridContains(rect: GridRectangle): Boolean =
    (rect.left >= 0 && rect.top >= 0 && rect.right <= width - 1 && rect.bottom <= height - 1)
  def gridContains(pos: GridLocation): Boolean = gridContains(new GridRectangle(pos, 1, 1))

  /** add structure (ie town, facility, rail..) to map */
  def addStructure(e: Renderable) = {
    for ((col, row) <- e.gridRect.iterateTuple)
      structuresLayer(col)(row) = Some(e)
  }
  /** test whether there is a structure at pos/in rect */
  def isUnused(pos: GridLocation): Boolean = gridContains(pos) && structuresLayer(pos.col)(pos.row) == None
  def isUnused(rect: GridRectangle): Boolean = rect.iterate.forall(isUnused)
  /** get structure at location if there is one */
  def maybeGetStructureAt(pos: GridLocation): Option[Renderable] = maybeGetStructureAt(pos.col, pos.row)
  def maybeGetStructureAt(col: Int, row: Int): Option[Renderable] = {
    if (gridContains(new GridLocation(col, row))) structuresLayer(col)(row)
    else None
  }
  /** return structures found in the 8 surrounding cases (modulo grid borders) */
  def getSurroundingStructures(pos: GridLocation, ind : Int ) : Array[Renderable] = {
    if (ind == 1) {
      Array(pos.top, pos.top.right, pos.right, pos.bottom.right,
          pos.bottom, pos.bottom.left, pos.left, pos.top.left)
          .filter(gridContains)
          .map(maybeGetStructureAt)
          .flatten
    } else {
        Array(pos.top, pos.right,
          pos.bottom,  pos.left)
          .filter(gridContains)
          .map(maybeGetStructureAt)
          .flatten
    }
  }
  def getSurroundingTiles(pos: GridLocation) : Array[Tile] = {
        Array(getBackgroundTile(pos.top),
        getBackgroundTile(pos.right),
        getBackgroundTile(pos.bottom),
        getBackgroundTile(pos.left))
  }

  /** add entity (ie train, plane, boat..) to map */
  def addEntity(e: Renderable) = entityLayer += e
  /** get set of all entities */
  def entities: ListBuffer[Renderable] = entityLayer
  /** get set of entities at location */
  def getEntitiesAt(pos: GridLocation): ListBuffer[Renderable] =
    entities filter { e: Renderable => e.gridPos.eq(pos) }

  /** set background tiles (ie grass, rock, tree, water, sand..) */
  def setBackgroundTile(pos: GridLocation, tile: Tile) = backgroundLayer(pos.col)(pos.row) = tile
  /** get tiles from background layer */
  def getBackgroundTile(col: Int, row: Int): Tile = backgroundLayer(col)(row)
  def getBackgroundTile(pos: GridLocation): Tile = getBackgroundTile(pos.col, pos.row)
  /** check whether tilemap at pos/rect is made only of tile(s) */
  def checkBgTile(pos: GridLocation, tile: Tile) : Boolean = backgroundLayer(pos.col)(pos.row) == tile
  def checkBgTile(pos: GridLocation, tiles: Array[Tile]) : Boolean = tiles.exists(checkBgTile(pos, _))
  def checkBgTile(col: Int, row: Int, tile: Tile) : Boolean = checkBgTile(new GridLocation(col, row), tile)
  def checkBgTile(col: Int, row: Int, tiles: Array[Tile]) : Boolean = tiles.exists(checkBgTile(col, row, _))
  def checkBgTiles(rect: GridRectangle, tiles: Array[Tile]) : Boolean = rect.iterate.forall(checkBgTile(_, tiles))

  /** randomly fill background layer of map using tiles */
  def fillBackground(tiles: Array[Tile]) : Unit = {
    val r = scala.util.Random
    if (tiles.length >= 1)
      new GridRectangle(0, 0, width, height)
      .iterate
      .foreach { setBackgroundTile(_, tiles(r.nextInt(tiles.length))) }
  }

  /** randomly add tile in background layer given a rate of apperance */
  def sprinkleTile(tile: Tile, percentage: Int) = {
    val r = scala.util.Random
    if (percentage >= 1 && percentage <= 100)
      new GridRectangle(0, 0, width, height)
      .iterate
      .filter { _ => r.nextInt(100) + 1 <= percentage }
      .filter { checkBgTile(_, Tile.Grass) }
      .filter { maybeGetStructureAt(_) == None}
      .foreach { setBackgroundTile(_, tile) }
  }






  // 2 ratios of points that will become lakes and points in teselation
  /* generate lakes with a random collection of central points for Voronoi tilings. Some Cells become lake randomly.*/
  def generateLakes(choosenPoint : Int, generatedPoints : Int) : Unit = {
    val r = scala.util.Random

    // add random points in lakeStarter at a rate of 1 / generatedPoints
    var lakeStarter = new ListBuffer[GridLocation]
    var riverStarter = new ListBuffer[GridLocation]
    for ((col, row) <- new GridRectangle(0, 0, width, height).iterateTuple)
      if (r.nextInt(generatedPoints) == 0)
        lakeStarter += new GridLocation(col, row)

    var teselationPoints : Array[ListBuffer[GridLocation]] = new Array(lakeStarter.size)
    // var lakeCenters = new ListBuffer[Int]
    for (i <- 0 to lakeStarter.size - 1)
      teselationPoints(i) = new ListBuffer[GridLocation]

    for ((col, row) <- new GridRectangle(0, 0, width, height).iterateTuple) {
      var distance = (height + width).toDouble
      var nearestPoint = 0
      var counter = 0
      for (pos <- lakeStarter) {
        var x = Math.abs(pos.row - row)
        var y = Math.abs(pos.col - col)
        var r = (Math.sqrt(Math.pow(x,2) + Math.pow(y,2)))
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
          backgroundLayer(pos.col)(pos.row) = Tile.Water(r.nextInt(Tile.Water.length))
        }
      }
    }
    for ((col, row) <- new GridRectangle(0, 0, width, height).iterateTuple) {
      // traiter les cas de bordure de lac pour rendre les bon tiles
      if (col > 0 && row > 0 && row< height -2 && col < width -2) {
        val neighbors = Array (
          new GridLocation(col, row - 1),
          new GridLocation(col + 1, row),
          new GridLocation(col, row + 1),
          new GridLocation(col - 1, row)
        )
        for (j <- 0 to 3) {
          if (checkBgTile(neighbors(j), Tile.Grass) && checkBgTile(col,row,Tile.Water)) {
            var randomPoint = scala.util.Random
            if (randomPoint.nextInt(50)==1) {
              riverStarter += (new GridLocation(col,row))
            }
            else backgroundLayer(col)(row) = Tile.Sand(r.nextInt(Tile.Sand.length))
          }
        }
      }
    }

    // Aptempt of river construction with bronian movment.
    for (pos <- riverStarter) {
      var position = pos
      var positions = new Array[GridLocation](4)
      var origine = r.nextInt(4)
      var source = false
      while(!source) {
        setBackgroundTile(position, Tile.Water(r.nextInt(Tile.Water.length)))
        var random = scala.util.Random
        if (random.nextInt(200) == 1){
          source = true
        }
        if (position.col > 0 && position.row > 0 && position.row< height -2 && position.col < width -2) {
          var previous = position
          positions = Array(position.top, position.right, position.bottom,  position.left)
          if (random.nextInt(2)==1) {
            position = positions(origine)
          }
          else{
            if (random.nextInt(32)==2) {
              position = positions((origine + 1) %4)
              origine = (origine + 1) % 4
            }else {
              if (random.nextInt(32)==1) {
                position = positions((origine + 3) % 4)
                origine = (origine + 3) % 4
              }else {
                // position = positions((origine - 2) %3)
                // origine = (origine - 2) %3
              }
            }
          }
          if (checkBgTile(position, Tile.Water) || (checkBgTile(position, Tile.Sand) )) {
            position = previous
          }
        }
      }
    }
  }
}
