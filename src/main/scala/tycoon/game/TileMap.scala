package tycoon.game

import tycoon.objects.landscape._
import tycoon.objects.graph.Map
import tycoon.ui.Tile
import tycoon.ui.Renderable
import scala.util.Random
import scalafx.geometry.Rectangle2D
import scala.collection.mutable.ListBuffer



class TileMap (val width: Int, val height: Int, nbEntityLayers: Int = 2) {
  private val backgroundLayer = Array.fill[Option[Tile]](width, height)(None)
  private val entities : Array[ListBuffer[Renderable]] = new Array(nbEntityLayers)
  for (i <- 0 to nbEntityLayers - 1)
    entities(i) = new ListBuffer[Renderable]

  def getBackgroundTile(col: Int, row: Int) = backgroundLayer(col)(row)
  def addBackgroundTile(col: Int, row: Int, tile: Tile) = backgroundLayer(col)(row) = Some(tile)

  def addEntity(e: Renderable, layer: Int = 0) = entities(layer) += e
  def removeEntity(e: Renderable, layer: Int = 0) = entities(layer) -= e
  def getEntities: Array[ListBuffer[Renderable]] = entities.clone()

  def gridContains(rect: GridRectangle) =
    (rect.left >= 0 && rect.top >= 0 && rect.right <= width - 1 && rect.bottom <= height - 1)

  def checkTile (pos : GridLocation, tile : Tile) : Boolean = {
    backgroundLayer(pos.col)(pos.row) match {
      case Some(t) => {
        if (t == tile) {return true}
        else {return false }
      }
      case None => {return false}
    }
  }

  def checkGrass (pos : GridLocation) : Boolean = {
    for (t <- Tile.grassAndGround) {
      if (checkTile(pos,t)){
        return true
      }
    }
    return false
  }

  /* randomly fill background layer of map using tiles
      and set Ores, trees and rocks*/
  def fillBackground(tiles: Array[Tile], map : Map) : Unit = {
    if (tiles.length >= 1) {
      val rGrass = scala.util.Random
      val rTreeAndRock = scala.util.Random
      // var lakeStarter = new ListBuffer[Int,Int]
      for {
        row <- 0 to height - 1
        col <- 0 to width - 1
      } {
        backgroundLayer(col)(row) = Some(tiles(rGrass.nextInt(tiles.length-2)))
        if (rTreeAndRock.nextInt(70) == 1) {
          backgroundLayer(col)(row) = Some(Tile.rock)
          // var rock = new Rock(new GridLocation(col, row),0)
          // addEntity(rock,0)
          // map.add(new GridRectangle(new GridLocation(col, row),1, 1),rock)
        }
        if (rTreeAndRock.nextInt(40) == 2) {
          // var tree = new Tree(new GridLocation(col, row),0)
          // addEntity(tree,0)
          // map.add(new GridRectangle(new GridLocation(col, row),1, 1),tree)
          backgroundLayer(col)(row) = Some(Tile.tree)
        }
        if (rTreeAndRock.nextInt(2000) == 3) {
          // var water = new Water(new GridLocation(col, row))
          // addEntity(water,0)
          // map.add(new GridRectangle(new GridLocation(col, row),1, 1),water)
          // lakeStarter += (col,row)
          backgroundLayer(col)(row) = Some(tiles(tiles.length-2))
        }
      }
      // for (water <- lakeStarter) {
      //   for {
      //   row <- 0 to height - 1
      //   col <- 0 to width - 1
      //   } {
      //     var x = row - water.gridPos.row
      //     var y = col - water.gridPos.col
      //     //disjoin borers and plain water..
      //     if ((x<10 && x > -10) && (y> -10 && y <10)) {
      //       var newWater = new Water(new GridLocation(col, row))
      //       addEntity(newWater,0)
      //       map.add(new GridRectangle(new GridLocation(col, row),1, 1),newWater)
      //     }
      //   }
      // }
    }
  }
}
