package tycoon.objects.structure

import scala.util.Random
import scala.collection.mutable.ListBuffer

import tycoon.objects.good._
import tycoon.game.GridLocation
import tycoon.game.Game
import tycoon.ui.Tile

case class Farm(pos: GridLocation, id: Int) extends Facility(pos, id) {
  tile = Tile.farm(0)
  var tileType = 0
  var production_time = 50
  var productionCounter = 0
  var haOfField = 1 //Maximum set at 10 ha of field around the farm

  var fields = new ListBuffer[Field]
  var productionPerPeriod = new ListBuffer[Int]

  // here are added new product

  /*In case of liquidWagon */
  // products += new Food("Milk")
  // productionPerPeriod += ()

  products += new Food("Egg")
  productionPerPeriod += (20)

  products += new Food("Corn")
  productionPerPeriod += (4)

  displayProducts()

  // update production

  def update_production(i: Int) = {
    stocksInt(i).set(stocks(i) + productionPerPeriod(i)*((workers*0.1 + 1).toInt)*haOfField)
  }

  override def update(dt: Double) = {
    intern_time += dt
    //for daily production
    if(intern_time > production_time) {
      productionCounter +=1
      //just for Corn, every 4 days
      if (productionCounter == 4) {
        if (tileType == 2) {
          update_production(1)
        }
        productionCounter = 0
        tileType = (tileType +1) % 3
        tile = Tile.farm(tileType)
        for (field <- fields) {
          field.tile = Tile.field(tileType)
        }
      }
      update_production(0)
      intern_time -= production_time
    }
  }
}
