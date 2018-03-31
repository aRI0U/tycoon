package tycoon.ui

import tycoon.game._

import scalafx.Includes._
import scalafx.geometry.{Pos, Insets}
import scalafx.scene.control._
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox, Priority}
import scalafx.beans.property._
import scalafx.scene.text.Text
import tycoon.objects.vehicle.train._
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.TableColumn._


class InteractionsMenu(val game: Game) extends TabPane
{
  stylesheets += "style/gamescreen.css"

  tabClosingPolicy = TabPane.TabClosingPolicy.Unavailable

  private val structuresTab = new Tab()
  private val roadsTab = new Tab()
  private val trainsTab = new Tab()

  structuresTab.text = "Build Structures"
  roadsTab.text = "Build Roads"
  trainsTab.text = "Manage Trains"

  this += structuresTab
  this += roadsTab
  this += trainsTab

  /**
   TABS FOR BUYING PHYSICAL OBJECTS (STRUCTURES, RAILS...)
  */

  private val buildingTabContainer = new Array[HBox](2)
  buildingTabContainer(0) = new HBox
  buildingTabContainer(1) = new HBox
  structuresTab.content = new ScrollPane { content = buildingTabContainer(0) }
  roadsTab.content = new ScrollPane { content = buildingTabContainer(1) }

  private var selectedBuildingItem: Option[BuyableItem] = None
  private var selectedBuildingItemTab: Option[Tab] = None

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
        selectedBuildingItem = Some(item)
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
    selectedBuildingItemTab = Some(itemTab)

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
        if(true) {                                                              /* TODO ALLOW TO REMOVE STRUCTS IN GAME */
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
    selectedBuildingItemTab match {
      case Some(tab) =>
        this.tabs -= tab
        tabPaneRequestFocus()
      case None => ()
    }
    selectedBuildingItemTab = None
  }

  private def stopBuilding() = {
    removeItemTab()
    selectedBuildingItem = None
  }


  /**
    TAB FOR MANAGING TRAINS & CARRIAGES
  */

  private val trainsTabContainer = new HBox()
  trainsTab.setContent(trainsTabContainer)

  val showTrainsBt = new Button {
    text <== StringProperty("Trains (").concat(game.nbTrains.asString) + " owned)"
    margin = Insets(10)
    vgrow = Priority.Always
    maxHeight = Double.MaxValue
    onMouseClicked = _ => openTrainsDataDialog()
  }

  trainsTabContainer.children = Seq(
    showTrainsBt
  )

  // when creating train
  // ask for train engine (determines speed, max carriages weight)
  // carriages (maybe a list of available carriages with a Add/Remove thing)
  // a town
  // also display the total price before validating

  // when creating route
  // choose start town
  // and also a add/remove last thing with the options available being the cities connected
  // (pas forcément juste à coté, le train peut aller de paris à lyon sans sarreter a clermond et continuer vers strasbourg)
  // et une ville peut meme etre choisie plusieurs fois (juste pas 2 fois a la suite)
  // OU choisir uniquement villes adjacentes et pour chacune choisir s'y on sy arrete ou pas
  // ensuite choisir un train dans startTown
  // et enfin choisir la répétition du trajet (une seule fois ou n fois, aller/retours tt le temps avec x temps de pause entre chaque retour
  // ou a chaque stop)

  // next stop | speed | weight | engine | nb passenger carriages | nb passagers
  // | passagers max | nb goods carriage | infos sur les goods | profits générés par ce train

  def openTrainsDataDialog() = {
    val trains = new ObservableBuffer[Train]
    trains ++= game.trains // filter on whether owner is player

    val idCol = new TableColumn[Train, String]("ID")
    idCol.minWidth = 30
    idCol.cellValueFactory = { cell => IntegerProperty(cell.value.id).asString }

    val stateCol = new TableColumn[Train, String]("STATE")
    stateCol.minWidth = 80
    stateCol.cellValueFactory = { cell => {
      val stateStr = StringProperty("")
      stateStr <== when (cell.value.moving) choose "Moving" otherwise "Stationary"
      stateStr
    }}

    val locationCol = new TableColumn[Train, String]("IN/FROM")
    locationCol.minWidth = 100
    locationCol.cellValueFactory = _.value.locationName

    val table = new TableView(trains)
    table.columns ++= Seq(idCol, stateCol, locationCol)


      /*

    val content = new TableView[Train](trains) {
      columns ++= List(
        new TableColumn[Train, String] {
          text = "State"
          cellValueFactory = { input => {
            val state = StringProperty("")
            state <== when(input.value.onTheRoad) choose "En chemin" otherwise "A l'arret"
            state
          }}
          minWidth = 50
        },
        new TableColumn[Train, String] {
          text = "From / In"
          cellValueFactory = { _.value.speed.asString } // create a StructureProperty...
          resizable = false
          editable = false
          minWidth = 100
        },
        new TableColumn[Train, String] {
          text = "Destination"
          cellValueFactory = { _.value.nbCarriages.asString }
          resizable = false
          editable = false
          minWidth = 100
        },
        new TableColumn[Train, String] {
          text = "Speed"
          cellValueFactory = { _.value.speed.asString }
          resizable = false
          editable = false
          minWidth = 200
        },
        new TableColumn[Train, String] {
          text = "Nb Carriages"
          cellValueFactory = { _.value.nbCarriages.asString }
          resizable = false
          editable = false
          minWidth = 200
        }
      )
    }*/

    val dialog = new Dialog
    dialog.title = "Your Trains"
    dialog.dialogPane.value.buttonTypes = Seq(ButtonType.Close)
    dialog.dialogPane().content = table
    dialog.showAndWait()
  }

  /* TODO  new Label("Nb of trains + others stats on them, button to see list of
   all trains with all their features (current trajet, nb passagers, engine, carriages..),
    bouton pour acheter nouveau train (locomotive), bouton pour acheter carriage, bouton pour gérer trains (carriages") */


  /* FUNCTIONS USED BY ALL TABS */

  private def selectFirstTab() = this.selectionModel.value.selectFirst()
  private def tabPaneRequestFocus() = this.requestFocus()

  def mousePressed(pos: GridLocation, dragging: Boolean = false): Unit = {
    // buying physical objects (structures, rails..)
    selectedBuildingItem match {
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
    // quit building when changing tab
    selectedBuildingItemTab match {
      case Some(tab) =>
        if (!tab.selected.value)
          stopBuilding()
      case None => ()
    }
  }
}
