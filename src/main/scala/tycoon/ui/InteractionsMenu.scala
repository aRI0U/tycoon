package tycoon.ui

import tycoon.game.{Game, GridLocation, BuyableItem, BuyableStruct, BuyableRail}

import scalafx.Includes._
import scalafx.geometry.{Pos, Insets}
import scalafx.scene.control.{Label, Tab, TabPane, Button, ScrollPane}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox, Priority}
import scalafx.beans.property.{StringProperty, IntegerProperty, DoubleProperty, ReadOnlyDoubleWrapper}
import scalafx.scene.text.Text



class InteractionsMenu(val game: Game) extends TabPane
{
  stylesheets += "style/gamescreen.css"

  tabClosingPolicy = TabPane.TabClosingPolicy.Unavailable

  private val buildingTab = new Tab()
  private val railsTab = new Tab()
  private val trainsTab = new Tab()

  buildingTab.text = "Build Structures"
  railsTab.text = "Buy Rails"
  trainsTab.text = "Manage Trains"

  this += buildingTab
  this += railsTab
  this += trainsTab

  private val buildingTabContainer = new Array[HBox](2)
  buildingTabContainer(0) = new HBox
  buildingTabContainer(1) = new HBox
  buildingTab.content = new ScrollPane {
    content = buildingTabContainer(0)
  }
  railsTab.content = new ScrollPane {
    content = buildingTabContainer(1)
  }

  private var selectedItem: Option[BuyableItem] = None
  private var selectedItemTab: Option[Tab] = None

  private val quantityBought = IntegerProperty(0)

  def addBuyableItem(item: BuyableItem, tabId: Int) = {
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
        quantityBought.set(0)
        addItemTab(item)
      }
    }
    buildingTabContainer(tabId).children += new VBox(itemBox)
  }

  def addBuyableStruct(item: BuyableStruct) = addBuyableItem(item, 0)
  def addBuyableRail(item: BuyableRail) = addBuyableItem(item, 1)

  private def addItemTab(item: BuyableItem) = {
    val itemTab = new Tab()
    itemTab.text = item.name + " Building"
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
        stopBuilding()
      }
    }
    val txtIndivPrice = new Text {
      text = "Price: $" + item.price
    }
    val txtQuantity = new Text {
      text <== StringProperty("Quantity: ").concat(quantityBought.asString)
    }
    val txtTotalPrice = new Text {
      text <== StringProperty("Total Cost: $").concat((quantityBought * item.price).asString)
    }
    val removeLastBt = new Button {
      text = "Resell Last"
      margin = Insets(10)
      vgrow = Priority.Always
      maxHeight = Double.MaxValue
      visible <== quantityBought > 0
      onMouseClicked = _ =>
        if(true) { // TODO REMOVE STRUCT IN GAME
          quantityBought.set(quantityBought.value - 1)
          tabPaneRequestFocus()
        }
    }

    val imgContainer = new VBox {
      children = Tile.getImageView(item.tile)
      alignment = Pos.Center
    }

    val buyingDataContainer = new VBox
    buyingDataContainer.alignment = Pos.CenterLeft
    buyingDataContainer.children += txtIndivPrice
    buyingDataContainer.children += txtQuantity
    buyingDataContainer.children += txtTotalPrice

    val container = new HBox(20.0)
    container.children += closeTabBt
    container.children += removeLastBt
    container.children += imgContainer
    container.children += buyingDataContainer

    itemTab.setContent(container)
  }

  private def removeItemTab() = {
    selectedItemTab match {
      case Some(tab) =>
        this.tabs -= tab
        tabPaneRequestFocus()
      case None => ()
    }
    selectedItemTab = None
  }

  private def stopBuilding() = {
    removeItemTab()
    selectedItem = None
  }

  private def selectFirstTab() = this.selectionModel.value.selectFirst()
  private def tabPaneRequestFocus() = this.requestFocus()

  def mousePressed(pos: GridLocation, dragging: Boolean = false): Unit = {
    selectedItem match {
      case Some(item) => {
        if(!dragging || item.createByDragging) {
          item match {
            case struct: BuyableStruct => {
              if(game.buyStruct(struct, pos))
                quantityBought.set(quantityBought.value + 1)
            }
            case rail: BuyableRail => {
              if(game.buyRail(rail, pos))
                quantityBought.set(quantityBought.value + 1)
            }
          }
        }
      }
      case None => ()
    }
  }

  this.selectionModel.value.selectedItem.onChange {
    selectedItemTab match {
      case Some(tab) =>
        if (!tab.selected.value)
          stopBuilding()
      case None => ()
    }
  }


  /**
    TRAINS TAB
  */

  private val trainsTabContainer = new HBox()
  trainsTab.setContent(trainsTabContainer)

  trainsTabContainer.children = Seq(
  // TODO  new Label("Nb of trains + others stats on them, button to see list of all trains with all their features (current trajet, nb passagers, engine, carriages..), bouton pour acheter nouveau train (locomotive), bouton pour acheter carriage, bouton pour gérer trains (carriages")
  )



}
