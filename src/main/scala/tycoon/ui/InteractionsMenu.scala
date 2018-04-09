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
import scala.collection.mutable.ListBuffer
import tycoon.objects.structure._
import tycoon.objects.railway._
import scalafx.scene.paint.Color
import tycoon.objects.graph.Route

class InteractionsMenu(val game: Game) extends TabPane
{
  stylesheets += "style/gamescreen.css"

  tabClosingPolicy = TabPane.TabClosingPolicy.Unavailable

  private val formatter = java.text.NumberFormat.getIntegerInstance

  private val structuresTab = new Tab()
  private val roadsTab = new Tab()
  private val vehiclesTab = new Tab()
  private val trainsTab = new Tab()

  structuresTab.text = "Build Structures"
  roadsTab.text = "Build Roads"
  vehiclesTab.text = "Buy Vehicles"
  trainsTab.text = "Manage Trains"

  this += structuresTab
  this += roadsTab
  this += vehiclesTab
  this += trainsTab

  private var currentTemporaryTab: Option[Tab] = None

  /**
   TABS FOR BUYING PHYSICAL OBJECTS (STRUCTURES, RAILS...)
  */

  private val buildingTabContainer = new Array[HBox](3)
  buildingTabContainer(0) = new HBox
  buildingTabContainer(1) = new HBox
  buildingTabContainer(2) = new HBox
  structuresTab.content = new ScrollPane { content = buildingTabContainer(0) }
  roadsTab.content = new ScrollPane { content = buildingTabContainer(1) }
  vehiclesTab.content = new ScrollPane { content = buildingTabContainer(2) }

  private var selectedBuyableItem: Option[BuyableItem] = None

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
        selectedBuyableItem = Some(item)
        quantityBought.set(0)
        addItemTab(item)
      }
    }
    buildingTabContainer(tabId).children += new VBox(itemBox)
  }

  def addBuyableStruct(item: BuyableStruct) = addBuyableItem(item, 0)
  def addBuyableRoad(item: BuyableRoad) = addBuyableItem(item, 1)
  def addBuyableVehicle(item: BuyableVehicle) = addBuyableItem(item, 2)

  private def addItemTab(item: BuyableItem) = {
    val itemTab = new Tab()
    itemTab.text = item.name + " Buying"
    this += itemTab
    this.selectionModel.value.selectLast()
    currentTemporaryTab = Some(itemTab)

    val closeTabBt = new Button {
      text = "Exit Buying"
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

    val removeLastBt = new Button { /* TODO */
      text = "Resell Last"
      margin = Insets(10)
      vgrow = Priority.Always
      maxHeight = Double.MaxValue
      visible <== quantityBought > 0
      onMouseClicked = _ => {
        if(true) {
          quantityBought.set(quantityBought.value - 1)
          tabPaneRequestFocus()
        }
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
    // container.children += removeLastBt /* button is not functional yet */
    container.children += imgContainer
    container.children += buyingDataContainer

    itemTab.setContent(container)
  }


  private def stopBuilding() = {
    removeTemporaryTab()
    selectedBuyableItem = None
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

  val newRouteBt = new Button {
    text = "New Route"
    margin = Insets(10)
    vgrow = Priority.Always
    maxHeight = Double.MaxValue
    onMouseClicked = _ => startTrainRouteCreation()
  }

  val showRoutesBt = new Button {
    text = "Manage Routes"
    margin = Insets(10)
    vgrow = Priority.Always
    maxHeight = Double.MaxValue
    onMouseClicked = _ => openManageRoutesDialog()
  }

  val buyCarriagesBt = new Button {
    text = "Buy Carriages"
    margin = Insets(10)
    vgrow = Priority.Always
    maxHeight = Double.MaxValue
    onMouseClicked = _ => openCarriagesBuyingDialog()
  }

  trainsTabContainer.children = Seq(
    showTrainsBt,
    newRouteBt,
    buyCarriagesBt,
    showRoutesBt
  )


  def openCarriagesBuyingDialog() = {
    val stoppedTrains: ListBuffer[Train] = game.trains filter (!_.moving.value)
    val stoppedTrainsTable: TableView[Train] = getTrainsTableView(stoppedTrains)
    stoppedTrainsTable.selectionModel.value.selectFirst()

    val passengerCarriagesSpinner = new Spinner[Integer](0, 5, 0)
    passengerCarriagesSpinner.maxWidth = 60
    val goodsCarriagesSpinner = new Spinner[Integer](0, 5, 0)
    goodsCarriagesSpinner.maxWidth = 60
    val tankCarsSpinner = new Spinner[Integer](0, 5, 0)
    goodsCarriagesSpinner.maxWidth = 60

    val passengerCarriageContent = new HBox(10)
    passengerCarriageContent.alignment = Pos.CenterLeft
    passengerCarriageContent.children += new Label("Add")
    passengerCarriageContent.children += passengerCarriagesSpinner
    passengerCarriageContent.children += new Label("Passenger Carriages (unit price: $" + PassengerCarriage.Price + ")")

    val goodsCarriageContent = new HBox(10)
    goodsCarriageContent.alignment = Pos.CenterLeft
    goodsCarriageContent.children += new Label("Add")
    goodsCarriageContent.children += goodsCarriagesSpinner
    goodsCarriageContent.children += new Label("Goods Carriages (unit price: $" + GoodsCarriage.Price + ")")

    val tankCarContent = new HBox(10)
    tankCarContent.alignment = Pos.CenterLeft
    tankCarContent.children += new Label("Add")
    tankCarContent.children += tankCarsSpinner
    tankCarContent.children += new Label("Tank Cars (unit price: $" + TankCar.Price + ")")

    var totalCost: Int = 0
    val totalCostStr = StringProperty("0")
    passengerCarriagesSpinner.value.onChange {
      totalCost = (passengerCarriagesSpinner.value.value * PassengerCarriage.Price
                  + goodsCarriagesSpinner.value.value * GoodsCarriage.Price
                + tankCarsSpinner.value.value * TankCar.Price)
      totalCostStr.set(formatter.format(totalCost))
    }
    goodsCarriagesSpinner.value.onChange {
      totalCost = (passengerCarriagesSpinner.value.value * PassengerCarriage.Price
                  + goodsCarriagesSpinner.value.value * GoodsCarriage.Price
                + tankCarsSpinner.value.value * TankCar.Price)
      totalCostStr.set(formatter.format(totalCost))
    }
    tankCarsSpinner.value.onChange {
      totalCost = (passengerCarriagesSpinner.value.value * PassengerCarriage.Price
                  + goodsCarriagesSpinner.value.value * GoodsCarriage.Price
                + tankCarsSpinner.value.value * TankCar.Price)
      totalCostStr.set(formatter.format(totalCost))
    }

    val totalPrice = new Text {
      text <== StringProperty("Total Cost: $").concat(totalCostStr)
      fill <== when (game.playerMoney >= totalCost) choose Color.Green otherwise Color.Red
    }

    val content = new VBox(10)
    content.children += passengerCarriageContent
    content.children += goodsCarriageContent
    content.children += tankCarContent
    content.children += new Label("To This Train:")
    content.children += stoppedTrainsTable
    content.children += totalPrice

    val dialog = new Dialog
    dialog.title = "Buy Carriages"
    dialog.dialogPane.value.buttonTypes = Seq(ButtonType.Finish, ButtonType.Cancel)
    dialog.dialogPane().content = content
    dialog.showAndWait() match {
      case Some(ButtonType.Finish) => {
        val train: Train = stoppedTrainsTable.selectionModel.value.selectedItem.value
        if (train == null)
          game.setInfoText("[Carriages Buying] You didn't select any train.")
        else if (train.moving.value)
          game.setInfoText("[Carriages Buying] You cannot add carriages to a moving train.")
        else {
          var bought: Boolean = true
          (1 to passengerCarriagesSpinner.value.value) foreach { _ => bought = bought && game.buyPassengerCarriage(train) }
          (1 to goodsCarriagesSpinner.value.value) foreach { _ => bought = bought && game.buyGoodsCarriage(train) }
          (1 to tankCarsSpinner.value.value) foreach { _ => bought = bought && game.buyTankCar(train) }
          if (bought)
            game.setInfoText("[Carriages Buying] The carriages have been bought.")
          else
            game.setInfoText("[Carriages Buying] All the carriages couldn't be bought. You reached the limit or didn't have enough money.")

        }
      }
      case _ => ()
    }
  }


  def getTrainsTableView(trainList: ListBuffer[Train]) : TableView[Train] = {
    val trains = new ObservableBuffer[Train]
    trains ++= trainList

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

    val nextLocationCol = new TableColumn[Train, String]("GOING TO")
    nextLocationCol.minWidth = 100
    nextLocationCol.cellValueFactory = _.value.nextLocationName

    val speedCol = new TableColumn[Train, String]("SPEED")
    speedCol.minWidth = 80
    speedCol.cellValueFactory = _.value.speed.asString.concat(" mph")

    // could add weight, nb of each type of carriages, nb passengers, nb max passengers, goods, profits the train made so far

    val table = new TableView(trains)
    table.columns ++= Seq(idCol, stateCol, locationCol, nextLocationCol, speedCol)
    table
  }


  def getRoutesTableView(routeList: ListBuffer[Route]) : TableView[Route] = {
    val routes = new ObservableBuffer[Route]
    routes ++= routeList

    val idCol = new TableColumn[Route, String]("TRAIN ID")
    idCol.minWidth = 30
    idCol.cellValueFactory = { cell => IntegerProperty(cell.value.train.id).asString }

    val stopsCol = new TableColumn[Route, String]("STOPS")
    stopsCol.minWidth = 300
    stopsCol.cellValueFactory = { cell =>
      StringProperty(cell.value.stops map { struct: Structure => struct.name } mkString ", ") }

    val table = new TableView(routes)
    table.minWidth = 400
    table.columns ++= Seq(idCol, stopsCol)
    table
  }

  def openManageRoutesDialog() = {
    val routes = getRoutesTableView(game.routes filter (_.repeated))

    val removeBt = new Button {
      text = "Remove Route"
      onMouseClicked = _ => {
        val route: Route = routes.selectionModel.value.selectedItem.value
        if (route == null)
          game.setInfoText("You didn't select any route.")
        else {
          route.repeated = false
          game.setInfoText("Route will be removed once the train reach its final stop.")
        }
      }
    }

    val content = new VBox(10)
    content.children.add(routes)
    content.children.add(removeBt)

    val dialog = new Dialog
    dialog.title = "Your Routes (only showing repeated routes)"
    dialog.dialogPane.value.buttonTypes = Seq(ButtonType.Close)
    dialog.dialogPane().content = content
    dialog.showAndWait()
  }

  def openTrainsDataDialog() = {
    val dialog = new Dialog
    dialog.title = "Your Trains"
    dialog.dialogPane.value.buttonTypes = Seq(ButtonType.Close)
    dialog.dialogPane().content = getTrainsTableView(game.trains)
    dialog.showAndWait()
  }

  def startTrainRouteCreation() = {
    val trainRouteTab = new Tab()
    trainRouteTab.text = "Train Route Creation"
    this += trainRouteTab
    this.selectionModel.value.selectLast()
    currentTemporaryTab = Some(trainRouteTab)

    val closeTabBt = new Button {
      text = "Cancel Creation"
      margin = Insets(10)
      vgrow = Priority.Always
      maxHeight = Double.MaxValue
      onMouseClicked = _ => {
        selectFirstTab()
        stopBuilding()
      }
    }

    val finishRouteBt = new Button {
      text = "Finish Route"
      margin = Insets(10)
      vgrow = Priority.Always
      maxHeight = Double.MaxValue
      onMouseClicked = _ => finishTrainRouteCreation()
    }

    val container = new HBox(20.0)
    container.children += closeTabBt
    container.children += finishRouteBt
    trainRouteTab.setContent(container)

    game.setInfoText("[Train Route Creation] Select the town where the journey shall begin!", -1)
    routeStops.clear()
    routeRoads.clear()
    creatingTrainRoute = true
    routeMaxSize = 10

  }

  private val routeStops = new ListBuffer[Structure]()
  private val routeRoads = new ListBuffer[Road]()
  private var creatingTrainRoute = false
  private var routeMaxSize = 0

  private def finishTrainRouteCreation() = {
    if (routeStops.length <= 1)
      game.setInfoText("[Train Route Creation] There wasn't enough stops to create a route.")
    else if (routeStops(0).trainList.isEmpty)
      game.setInfoText("[Train Route Creation] There wasn't enough trains to create a route.")
    else if (!routeStops.last.isInstanceOf[Town])
      game.setInfoText("[Train Route Creation] The last stop isn't a town. The route couldn't be created.")
    else {
      val trainsView: TableView[Train] = getTrainsTableView(routeStops(0).trainList)
      trainsView.selectionModel.value.selectFirst()
      val repeatRouteCb = new CheckBox("Repeat Route Indefinitely")
      repeatRouteCb.selected = false
      val content = new VBox(10)
      content.children ++= Seq(trainsView, repeatRouteCb)

      val dialog = new Dialog
      dialog.title = "Select a train for the route"
      dialog.dialogPane.value.buttonTypes = Seq(ButtonType.Finish, ButtonType.Cancel)
      dialog.dialogPane().content = content

      val routeTrain: Train = dialog.showAndWait() match {
        case Some(ButtonType.Finish) =>
          trainsView.selectionModel.value.selectedItem.value
        case _ => null
      }
      if (routeTrain == null)
        game.setInfoText("[Train Route Creation] You didn't select any train.")
      else {
        game.createRoute(routeRoads.clone(), routeStops.clone(), routeTrain, repeatRouteCb.selected.value)
        game.setInfoText("[Train Route Creation] The route has been created!")
      }
    }
    stopCreatingRoute()
  }

  private def stopCreatingRoute() = {
    removeTemporaryTab()
    routeStops.clear()
    routeRoads.clear()
    creatingTrainRoute = false
    game.clearInfoText(force = false)
  }




  /* TODO  new Label("Nb of trains + others stats on them, button to see list of
   all trains with all their features (current trajet, nb passagers, engine, carriages..),
    bouton pour acheter nouveau train (locomotive), bouton pour acheter carriage, bouton pour gérer trains (carriages") */


  /* FUNCTIONS USED BY ALL TABS */

  private def selectFirstTab() = this.selectionModel.value.selectFirst()
  private def tabPaneRequestFocus() = this.requestFocus()

  def mousePressed(pos: GridLocation, dragging: Boolean = false): Unit = {
    // buying physical objects (structures, rails..)
    selectedBuyableItem match {
      case Some(item) => {
        if(!dragging || item.createByDragging) {
          item match {
            case struct: BuyableStruct => {
              if(game.buyStruct(struct, pos))
                quantityBought.set(quantityBought.value + 1)
            }
            case rail: BuyableRoad => {
              if(game.buyRail(rail, pos))
                quantityBought.set(quantityBought.value + 1)
            }
            case vehicle: BuyableVehicle => {
              if(game.buyVehicle(vehicle, pos))
                quantityBought.set(quantityBought.value + 1)
            }
          }
        }
      }
      case None => ()
    }
  }

  def structureClicked(struct: Structure) {
    struct match {
      case town: Town => {
        if (creatingTrainRoute) {
          if (routeStops.isEmpty) {
            if (town.trainList.isEmpty) {
              game.setInfoText("[Train Route Creation] There is no train in this town. No train no gain!", -1)
            }
            else {
              routeStops += town
              game.setInfoText("[Train Route Creation] Now select from 1 to " + routeMaxSize.toString + " stops (last one must be a town).", -1)
            }
          }
          else if (routeStops.length < routeMaxSize && town != routeStops.last) {
            try {
              routeRoads ++= game.game_graph.shortestRoute(routeStops.last, town)
              routeStops += town
              game.setInfoText("[Train Route Creation] Now select from 1 to " + routeMaxSize.toString + " stops (last one must be a town) (" + (routeStops.length - 1).toString + ").", -1)
              if (routeStops.length + 1 == routeMaxSize)
                finishTrainRouteCreation()
            }
            catch {
              case e: IllegalStateException =>
                game.setInfoText("[Train Route Creation] This town cannot be reached from the previous stop. Trains can't fly!", -1)
            }
          }
        }
      }
      case facility: Facility => {
        if (creatingTrainRoute && routeStops.nonEmpty && routeStops.length < routeMaxSize && facility != routeStops.last) {
          try {
            routeRoads ++= game.game_graph.shortestRoute(routeStops.last, facility)
            routeStops += facility
            if (routeStops.length == routeMaxSize)
              finishTrainRouteCreation()
            game.setInfoText("[Train Route Creation] Now select from 1 to " + routeMaxSize.toString + " stops (" + (routeStops.length - 1).toString + ") (last one must be a town).", -1)
          }
          catch {
            case e: IllegalStateException =>
              game.setInfoText("[Train Route Creation] This facility cannot be reached from the previous stop. Trains can't fly!", -1)
          }
        }
      }
      case _ => ()
    }
  }

  private def removeTemporaryTab() = {
    currentTemporaryTab match {
      case Some(tab) =>
        this.tabs -= tab
        tabPaneRequestFocus()
        currentTemporaryTab = None
      case None => ()
    }
  }

  this.selectionModel.value.selectedItem.onChange {
    // quit building when changing tab
    currentTemporaryTab match {
      case Some(tab) =>
        if (!tab.selected.value) {
          stopBuilding()
          stopCreatingRoute()
        }
      case None => ()
    }
  }
}
