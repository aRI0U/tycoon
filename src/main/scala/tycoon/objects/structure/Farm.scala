package tycoon.objects.structure

import scala.util.Random
import scala.collection.mutable.ListBuffer

import tycoon.objects.good._
import tycoon.game._
import tycoon.ui.Tile

case class Farm(pos: GridLocation, id: Int, townManager: TownManager) extends Facility(pos, id, townManager) {
  tile = Tile.Farm(0)
  setName("Farm " + id.toString)
  var tileType = 0
  var productionTime = 50
  var productionCounter = 0
  var haOfField = 1 //Maximum set at 10 ha of field around the farm

  var fields = new ListBuffer[Field]
  var productionPerPeriod = new ListBuffer[Int]

  stock.newProduct(Product.Egg, 0)
  productionPerPeriod += (20)

  stock.newProduct(Product.Corn, 0)
  productionPerPeriod += (4)

  stock.newProduct(Product.Leather, 0)
  productionPerPeriod += (1)

  stock.newProduct(Product.Milk, 0)
  productionPerPeriod += (10)

  stock.newProduct(Product.RabbitFoot, 0)
  productionPerPeriod += (1)

  // update production

  def updateProduction(i: Int) = {
    stock.getMerchandiseWIndex(new Merchandise(stock.productsTypes(i), productionPerPeriod(i)*((1+workers/10).toInt)*haOfField, townManager.getTime()), i)
  }

  override def update(dt: Double) = {
    if (workers > 0) {
      internTime += dt
      //for daily production
      if(internTime > productionTime) {
        productionCounter +=1
        //just for Corn, every 4 days
        if (productionCounter == 4) {
          if (tileType == 2) {
            updateProduction(1) //Corn
          }
          productionCounter = 0
          tileType = (tileType +1) % 3
          tile = Tile.Farm(tileType)
          for (field <- fields) {
            field.tile = Tile.Field(tileType)
          }
        }
        updateProduction(0) //Eggs
        updateProduction(2) //Leather
        updateProduction(3) //Milk
        stock.updateExpiredProducts(townManager.getTime())
        internTime -= productionTime
      }
    }
  }
}
