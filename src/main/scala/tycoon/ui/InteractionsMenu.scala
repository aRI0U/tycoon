package tycoon.ui

import tycoon.game.{Game, GridLocation}

import scalafx.Includes._
import scalafx.geometry.{Pos, Insets}
import scalafx.scene.control.{Label, Tab, TabPane, Button, ScrollPane}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox, Priority}
import scalafx.beans.property.{StringProperty, IntegerProperty, DoubleProperty, ReadOnlyDoubleWrapper}
import scalafx.scene.text.Text

import tycoon.objects.structure.BuyableStruct


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


  /**
    BUILDING TAB
  */

  private val buildingTabContainer = new HBox()
  buildingTab.content = new ScrollPane {
    content = buildingTabContainer
  }

  private var selectedItem: Option[BuyableStruct] = None
  private var selectedItemTab: Option[Tab] = None

  private val quantityBought = IntegerProperty(0)

  def addBuyableStruct(item: BuyableStruct) = {
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
    buildingTabContainer.children += new VBox(itemBox)
  }

  private def addItemTab(item: BuyableStruct) = {
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
      case Some(item) =>
        if(!dragging || item.createByDragging)
          if(game.buyStruct(item, pos))
            quantityBought.set(quantityBought.value + 1)
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
