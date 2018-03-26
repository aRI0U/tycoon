package tycoon.ui

import tycoon.game.Game

import scalafx.Includes._
import scalafx.geometry.Pos
import scalafx.scene.control.{Label, Tab, TabPane}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox, Priority}

import tycoon.game.GridLocation


class BuyableItem(val name: String, val price: Int, val tile: Tile, val createItem: GridLocation => Boolean,
                  val removeLastItem: () => Boolean, val createByDragging: Boolean = false)
{
  def priceStr: String = "$" + price.toString
}
