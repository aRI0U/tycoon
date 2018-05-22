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



class Charts(val game: Game) extends Tab
{
  text = "Fancy Charts"

  private val p = game.player

  private val container = new HBox()
  this.setContent(container)

  def openChartDialog(content: Node) = {
    val dialog = new Dialog
    dialog.title = "Your finances"
    dialog.dialogPane.value.buttonTypes = Seq(ButtonType.Close)
    dialog.dialogPane().content = content
    dialog.showAndWait()
  }

  def getTextChart(): Chart = {
    new PieChart {
    }
  }

  def getProfitsPieChart(): Chart = {
    val dataPairs = Seq(("Trains", p.trainProfits), ("Planes", p.planeProfits), ("Boat", p.boatProfits))
    new PieChart {
      title = "Current Financial Report (Profits)"
      clockwise = false
      data = ObservableBuffer(dataPairs.map {case (x, y) => PieChart.Data(x, y)})
    }
  }
  def getExpensesPieChart(): Chart = {
    val dataPairs = Seq(("Trains", p.trainExpenses), ("Planes", p.planeExpenses), ("Boat", p.boatExpenses))
    new PieChart {
      title = "Current Financial Report (Expenses)"
      clockwise = false
      data = ObservableBuffer(dataPairs.map {case (x, y) => PieChart.Data(x, y)})
    }
  }

  def getLineChart(): Chart = {
    val dataPairs = Seq(("Alpha", 50), ("Beta", 80), ("RC1", 90), ("RC2", 30), ("1.0", 122), ("1.1", 10))
    new LineChart(CategoryAxis("X Axis"), NumberAxis("Y Axis")) {
      title = "Current Financial Report"
      data = XYChart.Series[String, Number](
        "Series 1",
        ObservableBuffer(dataPairs.map {case (x, y) => XYChart.Data[String, Number](x, y)})
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
    newChartBt("Text Chart", getTextChart _),
    newChartBt("Profits Pie Chart", getProfitsPieChart _),
    newChartBt("Expenses Pie Chart", getExpensesPieChart _),
    newChartBt("Line Chart", getLineChart _),
    newChartBt("Bar Chart", getBarChart _)
  )

}
