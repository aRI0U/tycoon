package tycoon.objects.structure

import scala.util.Random
import scala.collection.mutable.ListBuffer

import tycoon.objects.good._
import tycoon.game._
import tycoon.ui.Tile

case class WindMill(pos: GridLocation, id: Int, townManager: TownManager) extends Facility(pos, id, townManager) {
  tile = Tile.WindMill(0)
  setName("WindMill " + id.toString)
  var tileType = 0
  var productionTime = 0.1
  var productionCounter = 0

  var productionPerPeriod = new ListBuffer[Int]

  // update production

  def updateProduction(i: Int) = {
    stock.getMerchandiseWIndex(new Merchandise(stock.productsTypes(i), productionPerPeriod(i)*(1), townManager.getTime()), i)
  }

  override def update(dt: Double) = {
    // if (workers > 0) {
      internTime += dt
      if(internTime > productionTime) {
        tileType = (tileType +1) % Tile.WindMill.size
        tile = Tile.WindMill(tileType)
        internTime -= productionTime
        // stock.updateExpiredProducts(townManager.getTime())
      }
    // }
  }
}
