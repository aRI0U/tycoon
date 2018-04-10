package tycoon.game

import scala.collection.mutable.ListBuffer

import tycoon.objects.good.Good
import tycoon.objects.structure._

import scalafx.beans.property.{IntegerProperty, StringProperty}


class TownManager(game: Game) {

  // names
  var townNames = new ListBuffer[String]
  townNames += ("Paris", "Lyon", "Toulouse", "Saclay", "Nice", "Strasbourg", "Mulhouse", "Aulnay-sous-Bois", "Cachan", "Hamburg", "Berlin", "Brno", "Caderousse","Stuttgart", "Wien", "Köln")

  // information about towns
  var townsList = new ListBuffer[Town]
  var structuresList = new ListBuffer[Structure]

  def newStructure(structure: Structure) {
    for (t <- townsList) {
      // add the new structure to potential destinations
      if (structure != t)
        t.destinations += structure
      t.waitersInt += IntegerProperty(0)
    }
    for (s <- structuresList) {
      s match {
        case t: Town => t.newWeighting(s)
        case f: Facility => f.newWeighting(s)
        case _ => ()
      }
    }
    structuresList += structure
  }

  def newTown(town: Town) {
    newStructure(town)
    for (t <- townsList) t.printData(2).newRankedElement(town.name, t.waitersInt.last)
    town.displayWaiters()
    townsList += town
  }

  def getTime() : Double = game.totalElapsedTime

  def throwEvent(s: String) = game.setInfoText(s, 3)


  // ECONOMY

  val economicGoods = new ListBuffer[EconomicGood]

  def getEconomicGood(good: Good) : EconomicGood = {
    var i = 0
    while (i < economicGoods.length && economicGoods(i).kind != good) i += 1
    if (i == economicGoods.length) {
      economicGoods += new EconomicGood(good)
    }
    economicGoods(i)
  }

  def getReport(s: Structure, kind: Good, stocks: IntegerProperty, requests: IntegerProperty) = {
    val good = getEconomicGood(kind)
    good.newEmergence(s, stocks, requests)
    s match {
      case t: Town => {
        println("debug > town matched")
        t.newEconomicGood(good)
        println("debug > ok")
      }
      case f: Facility => f.newEconomicGood(good)
      case _ => ()
    }
  }
}
