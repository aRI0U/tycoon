package tycoon.objects.structure

import scala.util.Random
import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

import tycoon.game._
import tycoon.ui.Tile
import tycoon.objects.good._

case class Factory(pos: GridLocation, id: Int, townManager: TownManager) extends Facility(pos, id, townManager) {
  tile = Tile.Factory

  val productionTime = 5

  //Cake

  protected val _recipesList : ListBuffer[List[(Good, Int)]] = ListBuffer(
    List((Product.Egg,1),(Product.Corn,5)),
    List((Product.Iron,50)),
    List((Product.Iron,1),(Product.Milk,20)),
    List((Product.Leather,5)),
    List((Product.Sand,50)),
    List((Product.Oil,50)),
    List((Product.Iron,20),(Product.Gold,5),(Product.Leather,1)),
    List((Product.Corn,12))
  )

  def recipesList = _recipesList
  setName("Factory " + id.toString)


  // disponible products

  def addRecipeProducts(recipe: List[(Good, Int)]) = {
    for (p <- recipe) {
      stock.newProduct(p._1, 0)
    }
  }

  def initProducts() = {
    recipesList.foreach(r => addRecipeProducts(r))

    for (i <- 0 to recipesList.length-1)    addRecipeProducts(convertedInto(i))
  }

  initProducts()

  def convertedInto(recipeId: Int) : List[(Good,Int)] = {
    // possible conversions are hardcoded here
    recipeId match {
      case 0 => List((Product.Cake,1))
      case 1 => List((Product.Revolver,1))
      case 2 => List((Product.Cheese,1))
      case 3 => List((Product.Hat,1))
      case 4 => List((Product.Glass,1))
      case 5 => List((Product.Plastic,1))
      case 6 => List((Product.Ring,1))
      case 7 => List((Product.PopCorn,10))
      case _ => throw new IndexOutOfBoundsException
    }
  }

  def process(initialProducts: List[(Good,Int)], finalProducts: List[(Good,Int)]) = {
    // determine the indices of the useful products
    var usefulIndices = new ListBuffer[Int]
    for (product <- initialProducts) {
      usefulIndices += stock.getIndex(product._1)
    }
    usefulIndices = usefulIndices.filter(_ != -1)

    // determine how much could be processed
    var producedQuantity = workers
    for (j <- 0 to usefulIndices.length-1) {
      val i = usefulIndices(j)
      val m = stock.stocks(i)/initialProducts(j)._2
      if (m < producedQuantity) producedQuantity = m
    }
    // consume the initial products
    for (j <- 0 to usefulIndices.length-1) {
      val i = usefulIndices(j)
      var consumedQuantity = producedQuantity * initialProducts(j)._2
      var trash = new ListBuffer[Merchandise]
      stock.giveMerchandisesWIndex(i, stock.productsTypes(i), trash, consumedQuantity)
    }
    // create the final products

    for (p <- finalProducts) {
      stock.getMerchandise(new Merchandise(p._1, producedQuantity*p._2, townManager.getTime()))
    }
  }

  def updateProduction() = {
    for (i <- 0 to recipesList.length-1) {
      process(recipesList(i), convertedInto(i))
    }
  }

  override def update(dt: Double) = {
    if (workers > 0) {
      if (r.nextInt((7000-workers).max(100)) == 0) {
        val deads = 1+r.nextInt(workers.min(10))
        workers -= deads
        townManager.throwEvent("[Factory n°"+id+"] Industrial accident: "+deads+" workers tragically passed away...")
      }
      internTime += dt
      if (internTime > productionTime) {
        updateProduction()
        if (stock.updateExpiredProducts(townManager.getTime())) townManager.throwEvent("["+name+"] Be careful! Some of your food is expiring!")
        internTime -= productionTime
      }
    }
  }
}
