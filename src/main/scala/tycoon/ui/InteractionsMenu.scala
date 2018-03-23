package tycoon.ui

import tycoon.game._
import tycoon.objects.vehicle._
import tycoon.objects.railway._
import tycoon.objects.structure._

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.beans.property._
import scalafx.application.Platform
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{Stops, LinearGradient}
import scalafx.scene.layout.{BorderPane, VBox, StackPane, Pane}
import scalafx.scene.text.Text
import scalafx.geometry.{Pos, HPos, VPos, Insets, Rectangle2D, Orientation}
import scalafx.geometry.Orientation._
import scalafx.scene.control.{Button, Separator, ButtonType, Alert, TextArea, Label, Slider, Tab, TabPane}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{MouseEvent, KeyEvent}

import scala.collection.mutable.{HashMap, HashSet}
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer._ // Add, Remove, Reorder, Update

import scalafx.scene.control.{TextField, Button}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, HBox, VBox, GridPane, Priority}

import scalafx.beans.property.{StringProperty, IntegerProperty, BooleanProperty}
import scalafx.beans.binding.Bindings
import scala.collection.mutable.ListBuffer


class InteractionsMenu(val game: Game) extends TabPane
{
  tabClosingPolicy = TabPane.TabClosingPolicy.Unavailable

  private val buildTab = new Tab()
  private val trainsTab = new Tab()

  buildTab.text = "Build"
  trainsTab.text = "Manage trains"

  this += buildTab
  this += trainsTab

  private val buildTabContainer = new HBox()
  buildTab.setContent(buildTabContainer)

  private def buildBuyableItem(name: String, tile: Tile, price: String) = {
    new VBox {
      children = Seq(
        new Label(name),
        Tile.getImageView(tile),
        new Label(price))
      margin = Insets(10)
      alignment = Pos.Center
    }
  }
  private val towns = buildBuyableItem("Town", Tile.town, "$50,000")
  private val mines = buildBuyableItem("Mine", Tile.mine, "$200")
  private val rails = buildBuyableItem("Rail", Tile.straightRailBT, "$10")

  buildTabContainer.getChildren.add(towns)
  buildTabContainer.getChildren.add(mines)
  buildTabContainer.getChildren.add(rails)






}


/*
  val tab1: Tab = new Tab()
  tab1.text = "my tab 1"
  val hbox1 = new HBox()
  hbox1.getChildren().add(new Label("Tab1"))
  hbox1.setAlignment(Pos.Center)
  tab1.setContent(hbox1)
  val tab2: Tab = new Tab()
  tab2.text = "my tab 2"
  val hbox2 = new HBox()
  hbox2.getChildren().add(new Label("Tab2"))
  hbox2.setAlignment(Pos.Center)
  tab2.setContent(hbox2)*/
