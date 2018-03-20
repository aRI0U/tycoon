package tycoon.objects.structure

import scala.util.Random
import scala.collection.mutable.ListBuffer

import tycoon.objects.good._
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile

import scalafx.beans.property.{IntegerProperty, StringProperty}

// once built, a mine can produce a limited quantity of iron (iron_amount), the quantity of extracted iron depends on the number of diggers and on the quantity that has already been extracted (it is harder and harder to find iron)


case class Mine(pos: GridLocation, id: Int) extends Facility(pos, id) {
  protected val r = scala.util.Random

  protected var _diggers = IntegerProperty(0)

  private val diggersStr = new StringProperty
  diggersStr <== _diggers.asString
  printData += new Tuple2("Diggers", diggersStr)

  def diggers : Int = _diggers.get()
  def diggers_= (new_dig: Int) = _diggers.set(new_dig)

  val production_time = 10

  products += new Good("Iron")


  val iron_amount = 50 + r.nextInt(100)

  // val product = new Iron()

  //var production_per_period : Int = (diggers * (iron_amount - already_produced))/100





  tile = new Tile(Tile.mine)
  //val price = game.mine_price //To choose
}
