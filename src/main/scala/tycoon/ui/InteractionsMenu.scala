package tycoon.ui // #

import scala.collection.mutable.ListBuffer

import tycoon.game._
import tycoon.objects.graph._
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle.train._
import tycoon.objects.vehicle._

import scalafx.Includes._
import scalafx.beans.property._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Pos, Insets}
import scalafx.scene.control._
import scalafx.scene.control.TableColumn._
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox, Priority}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text


class InteractionsMenu(val game: Game) extends TabPane
{
  stylesheets += "style/gamescreen.css"

  tabClosingPolicy = TabPane.TabClosingPolicy.Unavailable

  private val formatter = java.text.NumberFormat.getIntegerInstance




  /** Create all tabs */

  private val structuresTab = new Tab()
  private val roadsTab = new Tab()
  private val vehiclesTab = new Tab()
  private val trainsTab = new Tab()
  private val routesTab = new Tab()

  structuresTab.text = "Build Structures"
  roadsTab.text = "Build Roads"
  vehiclesTab.text = "Buy Vehicles"
  trainsTab.text = "Manage Trains"
  routesTab.text = "Manage Routes"

  this += structuresTab
  this += roadsTab
  this += vehiclesTab
  this += trainsTab
  this += routesTab
  this += new Charts(game.player, "Your stats")
  this += new Charts(game.ai, "AI's stats")

  private var currentTemporaryTab: Option[Tab] = None

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
    // quit current action when switching tab
    currentTemporaryTab match {
      case Some(tab) =>
        if (!tab.selected.value) {
          stopBuilding()
          stopCreatingRoute()
        }
      case None => ()
    }
  }

  private def selectFirstTab() = this.selectionModel.value.selectFirst()
  private def tabPaneRequestFocus() = this.requestFocus()







  /**
    TABS FOR BUYING AND BUILDING (STRUCTURES, ROADS, VEHICLES...)
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
    container.children += imgContainer
    container.children += buyingDataContainer

    itemTab.setContent(container)
  }

  private def stopBuilding() = {
    removeTemporaryTab()
    selectedBuyableItem = None
  }

  // handle building physical objects (structures, rails..) and buying vehicles
  def mousePressed(pos: GridLocation, dragging: Boolean = false): Unit = {
    selectedBuyableItem match {
      case Some(item) => {
        if(!dragging || item.createByDragging) {
          item match {
            case struct: BuyableStruct => {
              if(game.buyStruct(struct, pos))
                quantityBought.set(quantityBought.value + 1)
            }
            case rail: BuyableRoad => {
              if(game.buyRoad(rail, pos))
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







  /**
    TAB FOR MANAGING TRAINS & CARRIAGES
  */

  private val trainsTabContainer = new HBox()
  trainsTab.setContent(trainsTabContainer)

  private val routesTabContainer = new HBox()
  routesTab.setContent(routesTabContainer)

  val showTrainsBt = new Button {
    text = "Your Trains"
    margin = Insets(10)
    vgrow = Priority.Always
    maxHeight = Double.MaxValue
    onMouseClicked = _ => openTrainsDataDialog()
  }

  val newRouteBt = new Button {
    text = "New Train Route"
    margin = Insets(10)
    vgrow = Priority.Always
    maxHeight = Double.MaxValue
    onMouseClicked = _ => startTrainRouteCreation()
  }

  val newPlaneTripBt = new Button {
    text = "Create Plane Trip"
    margin = Insets(10)
    vgrow = Priority.Always
    maxHeight = Double.MaxValue
    onMouseClicked = _ => startPlaneTripCreation()
  }

  val newBoatTripBt = new Button {
    text = "Create Boat Trip"
    margin = Insets(10)
    vgrow = Priority.Always
    maxHeight = Double.MaxValue
    onMouseClicked = _ => startBoatTripCreation()
  }

  val newTruckTripBt = new Button {
    text = "Create Truck Trip"
    margin = Insets(10)
    vgrow = Priority.Always
    maxHeight = Double.MaxValue
    onMouseClicked = _ => startTruckTripCreation()
  }

  val showRoutesBt = new Button {
    text = "Manage Routes"
    margin = Insets(10)
    vgrow = Priority.Always
    maxHeight = Double.MaxValue
    onMouseClicked = _ => openManageRoutesDialog()
  }

  val showTripsBt = new Button {
    text = "Manage Trips"
    margin = Insets(10)
    vgrow = Priority.Always
    maxHeight = Double.MaxValue
    onMouseClicked = _ => openManageTripsDialog()
  }

  val upgradeEngineBt = new Button {
    text = "Upgrade Engine"
    margin = Insets(10)
    vgrow = Priority.Always
    maxHeight = Double.MaxValue
    onMouseClicked = _ => openEngineUpgradingDialog()
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
    upgradeEngineBt,
    buyCarriagesBt
  )

  routesTabContainer.children = Seq(
    newRouteBt,
    showRoutesBt,
    showTripsBt,
    newPlaneTripBt,
    newBoatTripBt,
    newTruckTripBt
  )

  def openCarriagesBuyingDialog() = {
    val stoppedTrains: ListBuffer[Train] = game.trains filter (!_.moving.value)
    val stoppedTrainsTable: TableView[Train] = getTrainsTableView(stoppedTrains filter (_.owner == game.player))
    stoppedTrainsTable.selectionModel.value.selectFirst()

    val passengerCarriagesSpinner = new Spinner[Integer](0, 5, 0)
    passengerCarriagesSpinner.maxWidth = 60
    val goodsCarriagesSpinner = new Spinner[Integer](0, 5, 0)
    goodsCarriagesSpinner.maxWidth = 60
    val tankCarsSpinner = new Spinner[Integer](0, 5, 0)
    tankCarsSpinner.maxWidth = 60

    val passengerCarriageContent = new HBox(10)
    passengerCarriageContent.alignment = Pos.CenterLeft
    passengerCarriageContent.children += new Label("Add")
    passengerCarriageContent.children += passengerCarriagesSpinner
    passengerCarriageContent.children += new Label("Passenger Carriages (unit price: $" + Settings.CostPassengerCarriage + ")")

    val goodsCarriageContent = new HBox(10)
    goodsCarriageContent.alignment = Pos.CenterLeft
    goodsCarriageContent.children += new Label("Add")
    goodsCarriageContent.children += goodsCarriagesSpinner
    goodsCarriageContent.children += new Label("Goods Carriages (unit price: $" + Settings.CostGoodsCarriage + ")")

    val tankCarContent = new HBox(10)
    tankCarContent.alignment = Pos.CenterLeft
    tankCarContent.children += new Label("Add")
    tankCarContent.children += tankCarsSpinner
    tankCarContent.children += new Label("Tank Cars (unit price: $" + Settings.CostTankCar + ")")

    var totalCost: Int = 0
    val totalCostStr = StringProperty("0")
    def computeTotalCost() = {
      totalCost = (passengerCarriagesSpinner.value.value * Settings.CostPassengerCarriage
                  + goodsCarriagesSpinner.value.value * Settings.CostGoodsCarriage
                + tankCarsSpinner.value.value * Settings.CostTankCar)
      totalCostStr.set(formatter.format(totalCost))
    }

    passengerCarriagesSpinner.value.onChange { computeTotalCost() }
    goodsCarriagesSpinner.value.onChange { computeTotalCost() }
    tankCarsSpinner.value.onChange { computeTotalCost() }

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

  def openEngineUpgradingDialog() = {
    val upgradableTrains: ListBuffer[Train] = game.trains filter (_.engineUpgradeLevel.value < Engine.MaxUpgradeLevel)
    val upgradableTrainsTable: TableView[Train] = getTrainsTableView(upgradableTrains filter (_.owner == game.player))

    val costStr = StringProperty("")
    def updateCostStr() = {
      val currentTrain = upgradableTrainsTable.selectionModel.value.selectedItem.value
      if (currentTrain != null) {
        if (currentTrain.engineUpgradeLevel.value < Engine.MaxUpgradeLevel)
          costStr.set("Upgrade Cost: $" + Engine.Price(currentTrain.engineUpgradeLevel.value + 1).toString)
        else
          costStr.set("This train's engine cannot be upgraded anymore.")
      }
    }

    upgradableTrainsTable.selectionModel.value.selectedItem.onChange {
      updateCostStr()
    }

    val totalPrice = new Text {
      text <== costStr
    }

    val upgradeBt = new Button {
      text = "Upgrade Engine"
      onMouseClicked = _ => {
        val train: Train = upgradableTrainsTable.selectionModel.value.selectedItem.value
        if (train == null)
          game.setInfoText("[Train Engine Upgrading] You didn't select any train.")
        else {
          if (train.upgradeEngine())
            game.setInfoText("[Train Engine Upgrading] This train's engine has been upgraded.")
          else
            game.setInfoText("[Train Engine Upgrading] This train's engine couldn't be upgraded.")
        }
        updateCostStr()
      }
    }

    val content = new VBox(10)
    content.children += upgradableTrainsTable
    content.children += totalPrice
    content.children += upgradeBt

    val dialog = new Dialog
    dialog.title = "Upgrade Train Engine"
    dialog.dialogPane.value.buttonTypes = Seq(ButtonType.Finish)
    dialog.dialogPane().content = content
    dialog.showAndWait()
  }

  def getVehiclesTableView(vehList: ListBuffer[Vehicle]) : TableView[Vehicle] = {
    val vehicles = new ObservableBuffer[Vehicle]
    vehicles ++= vehList

    val idCol = new TableColumn[Vehicle, String]("ID")
    idCol.minWidth = 30
    idCol.cellValueFactory = { cell => IntegerProperty(cell.value.id).asString }

    val stateCol = new TableColumn[Vehicle, String]("STATE")
    stateCol.minWidth = 80
    stateCol.cellValueFactory = { cell => {
      val stateStr = StringProperty("")
      stateStr <== when (cell.value.moving) choose "Moving" otherwise "Stationary"
      stateStr
    }}

    val locationCol = new TableColumn[Vehicle, String]("IN/FROM")
    locationCol.minWidth = 100
    locationCol.cellValueFactory = _.value.locationName

    val nextLocationCol = new TableColumn[Vehicle, String]("GOING TO")
    nextLocationCol.minWidth = 100
    nextLocationCol.cellValueFactory = _.value.nextLocationName

    val speedCol = new TableColumn[Vehicle, String]("SPEED")
    speedCol.minWidth = 80
    speedCol.cellValueFactory = _.value.speed.asString.concat(" mph")

    val table = new TableView(vehicles)
    table.columns ++= Seq(idCol, stateCol, locationCol, nextLocationCol, speedCol)
    table
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

    val engineLevelCol = new TableColumn[Train, String]("ENGINE LEVEL")
    engineLevelCol.minWidth = 120
    engineLevelCol.cellValueFactory = _.value.engineUpgradeLevel.asString

    val table = new TableView(trains)
    table.columns ++= Seq(idCol, stateCol, locationCol, nextLocationCol, speedCol, engineLevelCol)
    table
  }

  def getRoutesTableView(routeList: ListBuffer[Route]) : TableView[Route] = {
    val routes = new ObservableBuffer[Route]
    routes ++= routeList

    val idCol = new TableColumn[Route, String]("ID")
    idCol.minWidth = 30
    idCol.cellValueFactory = { cell => IntegerProperty(cell.value.vehicle.id).asString }

    val typeCol = new TableColumn[Route, String]("TYPE")
    typeCol.minWidth = 50
    typeCol.cellValueFactory = { cell => cell.value.vehicle match {
      case _: Train => StringProperty("Train")
      case _: Boat => StringProperty("Boat")
      case _: Plane => StringProperty("Plane")
      case _: Truck => StringProperty("Truck")
      case _ => StringProperty("-")
    } }

    val stopsCol = new TableColumn[Route, String]("STOPS")
    stopsCol.minWidth = 300
    stopsCol.cellValueFactory = { cell =>
      StringProperty(cell.value.stops map { struct: Structure => struct.name } mkString ", ") }

    val table = new TableView(routes)
    table.minWidth = 500
    table.columns ++= Seq(idCol, typeCol, stopsCol)
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

  def getTripsTableView(tripList: ListBuffer[Trip]) : TableView[Trip] = {
    val trips = new ObservableBuffer[Trip]
    trips ++= tripList

    val idCol = new TableColumn[Trip, String]("ID")
    idCol.minWidth = 30
    idCol.cellValueFactory = { cell => IntegerProperty(cell.value.veh.id).asString }

    val typeCol = new TableColumn[Trip, String]("TYPE")
    typeCol.minWidth = 50
    typeCol.cellValueFactory = { cell => cell.value.veh match {
      case _: Train => StringProperty("Train")
      case _: Boat => StringProperty("Boat")
      case _: Plane => StringProperty("Plane")
      case _: Truck => StringProperty("Truck")
      case _ => StringProperty("-")
    } }

    val stopsCol = new TableColumn[Trip, String]("STOPS")
    stopsCol.minWidth = 150
    stopsCol.cellValueFactory = { cell =>
      StringProperty(cell.value.origin.name + ", " + cell.value.destination.name) }

    val table = new TableView(trips)
    table.minWidth = 400
    table.columns ++= Seq(idCol, typeCol, stopsCol)
    table
  }

  def openManageTripsDialog() = {
    val trips = getTripsTableView(game.trips filter (_.repeated))

    val removeBt = new Button {
      text = "Remove Trip"
      onMouseClicked = _ => {
        val trip: Trip = trips.selectionModel.value.selectedItem.value
        if (trip == null)
          game.setInfoText("You didn't select any trip.")
        else {
          trip.repeated = false
          game.setInfoText("Trip will be removed once the vehicle reach its final stop.")
        }
      }
    }

    val content = new VBox(10)
    content.children.add(trips)
    content.children.add(removeBt)

    val dialog = new Dialog
    dialog.title = "Your Trips (only showing repeated trips)"
    dialog.dialogPane.value.buttonTypes = Seq(ButtonType.Close)
    dialog.dialogPane().content = content
    dialog.showAndWait()
  }

  def openTrainsDataDialog() = {
    val dialog = new Dialog
    dialog.title = "Your Trains"
    dialog.dialogPane.value.buttonTypes = Seq(ButtonType.Close)
    dialog.dialogPane().content = getTrainsTableView(game.trains filter (_.owner == game.player))
    dialog.showAndWait()
  }









  /**
    ROUTE AND TRIP CREATION
  */


  private val routeStops = new ListBuffer[Structure]()
  private val routeRoads = new ListBuffer[Road]()
  private var creatingTrainRoute = false
  private var creatingPlaneTrip = false
  private var creatingBoatTrip = false
  private var creatingTruckTrip = false
  private var routeMaxSize = 0

  private var originStruct: Option[Structure] = None
  private var destinationStruct: Option[Structure] = None

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
        stopCreatingRoute()
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

  def addTripStruct(struct: Structure): Unit = {
    if (originStruct == None) {
      originStruct = Some(struct)
      game.setInfoText("[Trip Creation] Now select the destination of the trip.")
    } else if (struct != originStruct.get) {
      destinationStruct = Some(struct)
      endVehicleTripCreation()
    }
  }

  def startPlaneTripCreation(): Unit = {
    creatingPlaneTrip = true
    startVehicleTripCreation("Plane", "Airport")
  }
  def startBoatTripCreation(): Unit = {
    creatingBoatTrip = true
    startVehicleTripCreation("Boat", "Dock")
  }
  def startTruckTripCreation(): Unit = {
    creatingTruckTrip = true
    startVehicleTripCreation("Truck", "Structure")
  }

  def startVehicleTripCreation(vehName: String, structName: String) = {
    val tripTab = new Tab()
    tripTab.text = vehName + " Trip Creation"
    this += tripTab
    this.selectionModel.value.selectLast()
    currentTemporaryTab = Some(tripTab)

    val closeTabBt = new Button {
      text = "Cancel Creation"
      margin = Insets(10)
      vgrow = Priority.Always
      maxHeight = Double.MaxValue
      onMouseClicked = _ => {
        selectFirstTab()
        stopCreatingRoute()
      }
    }

    val container = new HBox
    container.children += closeTabBt
    tripTab.setContent(container)

    game.setInfoText("[" + vehName + " Trip Creation] Select the origin " + structName + ".", -1)
    originStruct = None
    destinationStruct = None
  }

  private def endVehicleTripCreation() = {
    val vehicles: ListBuffer[Vehicle] = {
      if (creatingPlaneTrip) originStruct.get.planeList
      else if (creatingBoatTrip) originStruct.get.boatList
      else if (creatingTruckTrip) originStruct.get.truckList
      else new ListBuffer[Vehicle]() }
    if (vehicles.isEmpty)
      game.setInfoText("[Trip Creation] No vehicle found for this trip.")
    else {
      val vehTable: TableView[Vehicle] = getVehiclesTableView(vehicles)
      vehTable.selectionModel.value.selectFirst()
      val repeatTripCb = new CheckBox("Repeat Route Indefinitely")
      repeatTripCb.selected = false
      val content = new VBox(10)
      content.children ++= Seq(vehTable, repeatTripCb)
      val dialog = new Dialog
      dialog.title = "Select a vehicle for the trip"
      dialog.dialogPane.value.buttonTypes = Seq(ButtonType.Finish, ButtonType.Cancel)
      dialog.dialogPane().content = content

      val tripVeh: Vehicle = dialog.showAndWait() match {
        case Some(ButtonType.Finish) =>
          vehTable.selectionModel.value.selectedItem.value
        case _ => null
      }
      if (tripVeh == null)
        game.setInfoText("[Trip Creation] You didn't select any vehicle.")
      else {
        game.createTrip(originStruct.get, destinationStruct.get, tripVeh, repeatTripCb.selected.value)
        game.setInfoText("[Trip Creation] The trip has been created!")
      }
    }

    stopCreatingRoute()
  }

  private def finishTrainRouteCreation() = {
    if (routeStops.length <= 1)
      game.setInfoText("[Train Route Creation] There wasn't enough stops to create a route.")
    else if (routeStops(0).trainList.isEmpty)
      game.setInfoText("[Train Route Creation] There wasn't enough trains to create a route.")
    else if (!routeStops.last.isInstanceOf[Town])
      game.setInfoText("[Train Route Creation] The last stop isn't a town. The route couldn't be created.")
    else {
      val trainsView: TableView[Train] = getTrainsTableView(routeStops(0).trainList filter (_.owner == game.player))
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
    creatingPlaneTrip = false
    creatingBoatTrip = false
    creatingTruckTrip = false
    game.clearInfoText(force = false)
  }

  def structureClicked(struct: Structure) {
    if (creatingTruckTrip)
      addTripStruct(struct)
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
              routeRoads ++= game.gameGraph.shortestRoute(routeStops.last, town)
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
            routeRoads ++= game.gameGraph.shortestRoute(routeStops.last, facility)
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
      case airport: Airport =>
        if (creatingPlaneTrip)
          addTripStruct(airport)
      case dock: Dock =>
        if (creatingBoatTrip)
          addTripStruct(dock)
      case _ => ()
    }
  }
}
