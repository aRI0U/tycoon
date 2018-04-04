package tycoon.game

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._

import scalafx.beans.property.{IntegerProperty, StringProperty}


class TownManager() {

  // names
  var townNames = new ListBuffer[String]
  townNames += ("Paris", "Lyon", "Toulouse", "Saclay", "Nice", "Strasbourg", "Mulhouse", "Aulnay-sous-Bois", "Cachan", "Hamburg", "Berlin", "Brno", "Caderousse","Stuttgart", "Wien", "Köln")
  var unchosenNames = townNames

  // information about towns
  var towns_list = new ListBuffer[Town]
  var last_town : Int = 0

  def newStructure(structure: Structure) {
    for (t <- towns_list) {
      // add the new structure to potential destinations
      if (structure != t)
        t.destinations += structure
      t.waitersInt += IntegerProperty(0)
      t.waitersStr += new StringProperty
      t.waitersStr.last <== t.waitersInt.last.asString
    }
  }

  def newTown(town: Town) {
    newStructure(town)
    for (t <- towns_list) t.printData += new Tuple2(town.name, t.waitersStr.last)
    town.displayWaiters()
    town.displayProducts()
    towns_list += town
    last_town += 1
  }

  // economy will be inserted here
}
