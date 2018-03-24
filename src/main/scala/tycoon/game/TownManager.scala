package tycoon.game

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure.Town

import scalafx.beans.property.{IntegerProperty, StringProperty}


class TownManager() {

  // names
  var town_names = new ListBuffer[String]
  town_names += ("Paris", "Lyon", "Toulouse", "Saclay", "Nice", "Strasbourg", "Mulhouse", "Aulnay-sous-Bois", "Cachan", "Hamburg", "Berlin", "Brno", "Caderousse","Stuttgart", "Wien", "Köln")
  var unchosen_names = town_names

  // information about towns
  var towns_list = new ListBuffer[Town]
  var last_town : Int = 0

  def newTown(town: Town) {
    town.displayWaiters()
    for (t <- towns_list) {
      // add the new town to potential destinations
      t.destinations += town
      t.waitersInt += IntegerProperty(0)
      t.waitersStr += new StringProperty
      t.waitersStr.last <== t.waitersInt.last.asString
      t.printData += new Tuple2(town.name, t.waitersStr.last)
    }
    towns_list += town
    last_town += 1
  }
}
