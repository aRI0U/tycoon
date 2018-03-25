package tycoon.ui

import tycoon.game.Game

import scalafx.Includes._
import scalafx.geometry.Pos
import scalafx.scene.control.{Label, Tab, TabPane}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox, Priority}


class InteractionsMenu(val game: Game) extends TabPane
{
  stylesheets += "style/gamescreen.css"

  tabClosingPolicy = TabPane.TabClosingPolicy.Unavailable

  private val buildTab = new Tab()
  private val trainsTab = new Tab()

  buildTab.text = "Build"
  trainsTab.text = "Manage trains"

  this += buildTab
  this += trainsTab

  /** BUILD "build" TAB */

  private val buildTabContainer = new HBox()
  buildTab.setContent(buildTabContainer)

  private def buildBuyableItem(name: String, tile: Tile, price: String, onClick: Runnable) = {
    new VBox {
      id = "buyableitems"
      children = Seq(
        new Label(name),
        Tile.getImageView(tile),
        new Label(price)
      )
      alignment = Pos.Center
      onMouseClicked = _ => onClick.run()
    }
  }
  private val towns = buildBuyableItem("Town", Tile.town, "$50,000", new Runnable { def run() {} })
  private val mines = buildBuyableItem("Mine", Tile.mine, "$200", new Runnable { def run() {} })
  private val rails = buildBuyableItem("Rail", Tile.straightRailBT, "$10", new Runnable { def run() {} })
  private val factories = buildBuyableItem("Factory", Tile.factory, "$10,000", new Runnable { def run() {} })
  private val farms = buildBuyableItem("Farm", Tile.farm, "$5,000", new Runnable { def run() {} })
  private val airports = buildBuyableItem("Airport", Tile.airport, "$100,000", new Runnable { def run() {} })

  buildTabContainer.children = Seq(towns, mines, rails, factories, farms, airports)

  /** BUILD "trains" TAB */

  private val trainsTabContainer = new HBox()
  trainsTab.setContent(trainsTabContainer)

  trainsTabContainer.children = Seq(
  // TODO  new Label("Nb of trains + others stats on them, button to see list of all trains with all their features (current trajet, nb passagers, engine, carriages..), bouton pour acheter nouveau train (locomotive), bouton pour acheter carriage, bouton pour gérer trains (carriages")
  )



}
