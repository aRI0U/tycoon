package tycoon.ui

import tycoon.game.{Game, GridLocation}

import scalafx.Includes._
import scalafx.geometry.{Pos, Insets}
import scalafx.scene.control.{Label, Tab, TabPane, Button}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox, Priority}




class InteractionsMenu(val game: Game) extends TabPane
{
  stylesheets += "style/gamescreen.css"

  tabClosingPolicy = TabPane.TabClosingPolicy.Unavailable

  private val buildingTab = new Tab()
  private val trainsTab = new Tab()

  buildingTab.text = "Build Structures"
  trainsTab.text = "Manage Trains"

  this += buildingTab
  this += trainsTab

  /** "build" TAB */

  private val buildingTabContainer = new HBox()
  buildingTab.setContent(buildingTabContainer)

  private var selectedItem: Option[BuyableItem] = None
  private var selectedItemTab: Option[Tab] = None

  def addBuyableItem(item: BuyableItem) = {
    val itemBox = new VBox {
      styleClass += "buyableItem"
      children = Seq(
        new Label(item.name),
        Tile.getImageView(item.tile),
        new Label(item.priceStr)
      )
      alignment = Pos.Center
      onMouseClicked = _ => {
        selectedItem = Some(item)
        addItemTab(item)
      }
    }
    buildingTabContainer.children += new VBox(itemBox)
  }

  private def addItemTab(item: BuyableItem) = {
    val itemTab = new Tab()
    itemTab.text = item.name + " building"
    this += itemTab
    this.selectionModel.value.selectLast()
    selectedItemTab = Some(itemTab)

    val closeTabBt = new Button {
      text = "Exit Construction"
      margin = Insets(10)
      vgrow = Priority.Always
      maxHeight = Double.MaxValue
      onMouseClicked = _ => {
        selectFirstTab()
        removeItemTab()
        selectedItem = None
      }
    }
    itemTab.setContent(closeTabBt)
  }

  private def removeItemTab() = {
    selectedItemTab match {
      case Some(tab) =>
        this.tabs -= tab
        this.requestFocus
      case None => ()
    }
    selectedItemTab = None
  }

  private def selectFirstTab() = this.selectionModel.value.selectFirst()

  def mousePressed(pos: GridLocation, dragging: Boolean = false): Unit = {
    selectedItem match {
      case Some(item) =>
        if(!dragging || item.createByDragging) item.onClick(pos)
      case None => ()
    }
  }

  this.selectionModel.value.selectedItem.onChange {
    selectedItemTab match {
      case Some(tab) =>
        if (!tab.selected.value) {
          removeItemTab()
          selectedItem = None
        }
      case None => ()
    }
  }


  /** "trains" TAB */

  private val trainsTabContainer = new HBox()
  trainsTab.setContent(trainsTabContainer)

  trainsTabContainer.children = Seq(
  // TODO  new Label("Nb of trains + others stats on them, button to see list of all trains with all their features (current trajet, nb passagers, engine, carriages..), bouton pour acheter nouveau train (locomotive), bouton pour acheter carriage, bouton pour gérer trains (carriages")
  )



}
