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
import scalafx.scene.layout.{HBox, VBox, Priority}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.scene.control._
import scalafx.scene.chart._
import scalafx.scene.Node

import java.util.Date
import java.text.SimpleDateFormat



class Charts(val game: Game) extends Tab
{
  text = "Fancy Charts"

  private val p = game.player

  private val container = new HBox()
  this.setContent(container)

  def openChartDialog(content: Node) = {
    val dialog = new Dialog
    dialog.title = "Current Financial Report"
    dialog.dialogPane.value.buttonTypes = Seq(ButtonType.Close)
    dialog.dialogPane().content = content
    dialog.showAndWait()
  }

  def getTextChart(): VBox = {
    new VBox {
      minWidth = 400
      children = Seq(
        new Text("PROFITS (PASSENGER AND GOOD TRANSPORT)"),
        new Text("Trains: $" + p.trainProfits),
        new Text("Boats: $" + p.boatProfits),
        new Text("Planes: $" + p.planeProfits),
        new Text(""),
        new Text("EXPENSES (FUEL AND MAINTENANCE)"),
        new Text("Trains: $" + p.trainExpenses),
        new Text("Boats: $" + p.boatExpenses),
        new Text("Planes: $" + p.planeExpenses),
        new Text(""),
        new Text("SPENDINGS"),
        new Text("Structures: $" + p.structSpendings),
        new Text("Roads: $" + p.roadSpendings),
        new Text("Vehicles: $" + p.vehicleSpendings),
        new Text(""),
        new Text("RANDOM"),
        new Text(p.nbRoadsBuilt + " miles of road placed"),
      )
    }
  }

  def getPieChart(): VBox = {
    var dataPairs = Seq(("Trains", p.trainProfits), ("Planes", p.planeProfits), ("Boat", p.boatProfits))
    val profitsPie = new PieChart {
      title = "Profits"
      clockwise = false
      data = ObservableBuffer(dataPairs.map {case (x, y) => PieChart.Data(x, y)})
    }
    dataPairs = Seq(("Trains", p.trainExpenses), ("Planes", p.planeExpenses), ("Boat", p.boatExpenses))
    val expensesPie = new PieChart {
      title = "Expenses"
      clockwise = false
      data = ObservableBuffer(dataPairs.map {case (x, y) => PieChart.Data(x, y)})
    }
    dataPairs = Seq(("Structures", p.structSpendings), ("Vehicles", p.vehicleSpendings), ("Roads", p.roadSpendings))
    val spendingsPie = new PieChart {
      title = "Spendings"
      clockwise = false
      data = ObservableBuffer(dataPairs.map {case (x, y) => PieChart.Data(x, y)})
    }
    new VBox {
      children = Seq(
        new HBox {
          children = Seq(profitsPie, expensesPie)
        },
        spendingsPie
      )
    }
  }

  def getLineChart(): Chart = {
    val dataPairsMoney = for (i <- 0 until p.moneyMonitoring.length) yield (i.toString, p.moneyMonitoring(i))
    val dataPairsTrains = for (i <- 0 until p.trainsMoneyMonitoring.length) yield (i.toString, p.trainsMoneyMonitoring(i))
    val dataPairsBoats = for (i <- 0 until p.boatsMoneyMonitoring.length) yield (i.toString, p.boatsMoneyMonitoring(i))
    val dataPairsPlanes = for (i <- 0 until p.planesMoneyMonitoring.length) yield (i.toString, p.planesMoneyMonitoring(i))
    new LineChart(CategoryAxis("Time"), NumberAxis("Net income ($)")) {
      title = "Real-Time Financial Report"
      createSymbols = false
      horizontalGridLinesVisible = false
      verticalGridLinesVisible = false
      data = ObservableBuffer(
        XYChart.Series[String, Number](
          "Global",
          ObservableBuffer(dataPairsMoney.map {case (x, y) => XYChart.Data[String, Number](x, y)})
        ),
        XYChart.Series[String, Number](
          "Trains",
          ObservableBuffer(dataPairsTrains.map {case (x, y) => XYChart.Data[String, Number](x, y)})
        ),
        XYChart.Series[String, Number](
          "Boats",
          ObservableBuffer(dataPairsBoats.map {case (x, y) => XYChart.Data[String, Number](x, y)})
        ),
        XYChart.Series[String, Number](
          "Planes",
          ObservableBuffer(dataPairsPlanes.map {case (x, y) => XYChart.Data[String, Number](x, y)})
        ),
      )
    }
  }

  def getBarChart(): Chart = {
    val vehs = Seq("Trains", "Boats", "Planes")
    def xySeries(name: String, data: Seq[Int]) = {
      val series = vehs zip data
      XYChart.Series[String, Number](
        name,
        ObservableBuffer(series.map {case (x, y) => XYChart.Data[String, Number](x, y)})
      )
    }
    new BarChart(CategoryAxis(), NumberAxis("Dollars")) {
      title = "Current Financial Report"
      data = ObservableBuffer(
        xySeries("Profits", Seq(p.trainProfits, p.boatProfits, p.planeProfits)),
        xySeries("Expenses", Seq(p.trainExpenses, p.boatExpenses, p.planeExpenses)),
      )
    }
  }

  def newChartBt(name: String, getContent: () => Node): Button = {
    new Button {
      text = name
      margin = Insets(10)
      vgrow = Priority.Always
      maxHeight = Double.MaxValue
      onMouseClicked = _ => openChartDialog(getContent())
    }
  }

  container.children = Seq(
    newChartBt("Stats", getTextChart _),
    newChartBt("Pie Charts", getPieChart _),
    newChartBt("Line Chart", getLineChart _),
    newChartBt("Bar Chart", getBarChart _)
  )

}
