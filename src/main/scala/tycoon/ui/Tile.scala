package tycoon.ui

import tycoon.game.GridLocation

import scalafx.geometry.Rectangle2D
import scalafx.scene.CacheHint
import scalafx.scene.image.{Image, ImageView}


class Tile(val name: String, row: Int, col: Int, val width: Int = 1, val height: Int = 1) {
  // source rectangle's coordinates and size in Tile.tileset
  val sx = (col - 1) * Tile.SquareWidth
  val sy = (row - 1) * Tile.SquareHeight
  val sw = width * Tile.SquareWidth
  val sh = height * Tile.SquareHeight
}

object Tile {
  val tileset = new Image("file:src/main/resources/tileset.png")

  val SquareWidth = 32
  val SquareHeight = 32

  def getImageView(t: Tile) : ImageView = {
    val img = new ImageView(tileset)
    img.viewport = new Rectangle2D(t.sx, t.sy, t.sw, t.sh)
    img
  }

  val Default = new Tile("default", 0, 0)

  // vehicles
  val Boat = new Tile("boat", 6, 4)
  val Plane = new Tile("plane", 6, 2)
  val Truck = new Tile("truck", 8, 6)

  // train wagons
  val LocomotiveL = new Tile("locomotiveL", 3, 5)
  val LocomotiveB = new Tile("locomotiveB", 3, 2)
  val LocomotiveR = new Tile("locomotiveR", 4, 5)
  val LocomotiveT = new Tile("locomotiveT", 3, 6)
  val PassengerWagonT = new Tile("passengerWagonT", 4, 6)
  val PassengerWagonB = new Tile("passengerWagonB", 3, 1)
  val PassengerWagonL = new Tile("passengerWagonL", 5, 6)
  val PassengerWagonR = new Tile("passengerWagonR", 6, 6)
  val GoodsWagonR = new Tile("goodsWagonR", 7, 5)
  val GoodsWagonB = new Tile("goodsWagonB", 3, 3)
  val GoodsWagonL = new Tile("goodsWagonL", 7, 4)
  val GoodsWagonT = new Tile("goodsWagonT", 7, 6)
  val LiquidWagon = new Tile("liquidWagon", 6, 1)

  // structures
  val Town = new Tile("town", 4, 1, width = 2)
  val Mine = new Tile("mine", 4, 3)
  val Factory = new Tile("factory", 5, 1)
  val PackingPlant = new Tile("packing plant", 5, 1)
  val Airport = new Tile("airport", 6, 3)
  val Dock = new Tile("dock", 4, 7)
  val Field = Array(
    new Tile("field1", 2, 8),
    new Tile("field2", 1, 8),
    new Tile("field3", 3, 8)
  )
  val Farm = Array(
    new Tile("farm1", 2, 7, width = 2),
    new Tile("farm2", 1, 7, width = 2),
    new Tile("farm3", 3, 7, width = 2)
  )

  // rails
  val StraightRailBT = new Tile("straightRailBT", 1, 1)
  val StraightRailLR = new Tile("straightRailLR", 2, 6)
  val TurningRailBR = new Tile("turningRailBR", 1, 2)
  val TurningRailBL = new Tile("turningRailBL", 1, 6)
  val TurningRailTR = new Tile("turningRailTR", 1, 5)
  val TurningRailTL = new Tile("turningRailTL", 2, 5)

  // background tiles
  val Tree = new Tile("tree", 3, 4)
  val Rock = new Tile("rock", 4, 4)
  val Asphalt = new Tile("asphalt", 8, 7)
  val Sand = Array(
    new Tile("sand",4,8),
    new Tile("sand",5,8),
    new Tile("sand",5,7)
  )
  val Water = Array(
    new Tile("water",6,5),
    new Tile("water",5,5),
    new Tile("water",5,4)
  )
  val Grass = Array(
    new Tile("grass", 1, 3),
    new Tile("grass", 1, 4),
    new Tile("grass", 2, 1),
    new Tile("grass", 2, 2),
    new Tile("grass", 2, 3),
    new Tile("grass", 2, 4)
  )
}
