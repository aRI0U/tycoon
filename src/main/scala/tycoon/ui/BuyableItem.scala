package tycoon.ui

import tycoon.game.Game

import scalafx.Includes._
import scalafx.geometry.Pos
import scalafx.scene.control.{Label, Tab, TabPane}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox, Priority}

import tycoon.game.GridLocation


class BuyableItem(val name: String, price: Int, val tile: Tile, val onClick: GridLocation => Boolean, val createByDragging: Boolean = false)
{
  def priceStr: String = "$" + price.toString
}
