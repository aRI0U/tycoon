package tycoon.objects.structure

import scala.collection.mutable.ListBuffer
import scala.util.Random

import tycoon.objects.vehicle._
import tycoon.objects.vehicle.train._
import tycoon.objects.good._
import tycoon.ui.Renderable
import tycoon.game.GridLocation

import scalafx.beans.property.{IntegerProperty, StringProperty}


abstract class Structure(pos: GridLocation, id: Int) extends Renderable(pos) {
  val structureId = id
  var trainList = new ListBuffer[Train]()
  var planeList = new ListBuffer[Plane]()

  protected val r = scala.util.Random

  protected val _name = StringProperty("")
  def name: String = _name.value
  def setName(s : String) = _name.set(s)

  var internTime: Double = 0
  def update(dt: Double)

  def addTrain(train: Train) = trainList += train
  def addPlane(plane: Plane) = planeList += plane
  def removeTrain(train: Train) = trainList -= train
  def getTrain: Option[Train] = trainList.lastOption
  def getPlane: Option[Plane] = planeList.lastOption

  val stock = new Stock(this)

  // // products present in a structure
  // var products = new ListBuffer[Good]
  // var datedProducts = new ListBuffer[ListBuffer[Merchandise]]
  // var stocksInt = new ListBuffer[IntegerProperty]
  // var stocksStr = new ListBuffer[StringProperty]
  //
  // def stocks(i: Int) : Int = stocksInt(i).value
  // def stocks_= (i: Int, new_stock: Int) = stocksInt(i).set(new_stock)
  //

  //
  // def updateStocks() = {
  //   for (i <- 0 to stocksInt.length - 1) {
  //     var totalQuantity = 0
  //     for (m <- datedProducts(i)) totalQuantity += m.quantity
  //     stocksInt(i).set(totalQuantity)
  //   }
  // }
  //
  // def updateExpiredProducts(currentTime: Double) = {
  //   for (merchList <- datedProducts) {
  //     for (m <- merchList) {
  //       m.expiryDate match {
  //         case Some(time) => {
  //           if (currentTime > time) {
  //             merchList -= m
  //             throwEvent("Be careful! "+m.kind.label+"s expire if you wait too long")
  //           }
  //         }
  //         case None => ()
  //       }
  //     }
  //   }
  //   updateStocks()
  // }

  def throwEvent(s: String) {
    throw new EventException(s)
  }

  // def areSameGoods(g1: Good, g2: Good) : Boolean = {
  //   (g1.label == g2.label)
  // }
}

class EventException(val s: String) extends Exception {}
