package tycoon.ui // #

import scala.collection.mutable.ListBuffer

import scalafx.scene.image.{Image, ImageView}
import scalafx.beans.property._


class PrintableElement(val name: String, val valueInt: IntegerProperty) {
  val valueStr = new StringProperty
  valueStr <== valueInt.asString
  def icon : ImageView = {
    name match {
      case "Corn" => Tile.getImageView(Tile.Corn)
      case "Egg" => Tile.getImageView(Tile.Egg)
      case "Cake" => Tile.getImageView(Tile.Cake)
      case "Bread" => Tile.getImageView(Tile.Bread)
      case "Sand" => Tile.getImageView(Tile.Sands)
      case "Milk" => Tile.getImageView(Tile.Milk)
      case "PopCorn" => Tile.getImageView(Tile.PopCorn)
      case "Gold" => Tile.getImageView(Tile.Gold)
      case "Coal" => Tile.getImageView(Tile.Coal)
      case "Iron" => Tile.getImageView(Tile.Iron)
      case "RabbitFoot" => Tile.getImageView(Tile.RabbitFoot)
      case "Cheese" => Tile.getImageView(Tile.Cheese)
      case "Population" => Tile.getImageView(Tile.Person)
      case "JobSeekers" => Tile.getImageView(Tile.Job)
      case _ => Tile.getImageView(Tile.Default)
    }
  }
}

class PrintableRankedElement(override val name: String, override val valueInt: IntegerProperty)
extends PrintableElement(name, valueInt) {
  val visible = new BooleanProperty
  visible <== valueInt =!= IntegerProperty(0)

  val rangeInt = ListBuffer(IntegerProperty(0))
  val range = new IntegerProperty

  def initRange(data: ListBuffer[PrintableElement]) : Unit = {
    for (d <- data) {
      val b = new BooleanProperty
      b <== (valueInt > d.valueInt)

      val aux = new IntegerProperty
      aux <== rangeInt.last

      rangeInt += new IntegerProperty
      rangeInt.last <== aux + boolToInt(b)
      range <== rangeInt.last
    }
  }

  def newElement(d: PrintableRankedElement) : Unit = {
    val b = new BooleanProperty
    b <== (valueInt > d.valueInt)

    val aux = new IntegerProperty
    aux <== rangeInt.last

    rangeInt += new IntegerProperty
    rangeInt.last <== aux + boolToInt(b)
    range <== rangeInt.last
  }

  def boolToInt(b: BooleanProperty): IntegerProperty = {
    if (b.value) IntegerProperty(1)
    else IntegerProperty(0)
  }
}

class PrintableTownProduct(override val name: String, override val valueInt: IntegerProperty, val price: DoubleProperty)
extends PrintableRankedElement(name, valueInt) {
  valueStr <== valueInt.asString.concat(" for $").concat(price.asString)
}

class PrintableData(s: String) {
  val label: String = s
  val data = new ListBuffer[PrintableElement]

  def newElement(name: String, property: IntegerProperty) : Unit =
    data += new PrintableElement(name, property)

  def newRankedElement(name: String, property: IntegerProperty) : Unit = {
    val d = new PrintableRankedElement(name, property)
    d.initRange(data)

    data foreach {
      elt => elt match {
        case rankedElt: PrintableRankedElement => rankedElt.newElement(d)
        case _ => ()
      }
    }
    data += d
  }

  def newTownProduct(name: String, property: IntegerProperty, price: DoubleProperty) : Unit = {
    val d = new PrintableTownProduct(name, property, price)
    d.initRange(data)

    data foreach {
      elt => elt match {
        case rankedElt: PrintableRankedElement => rankedElt.newElement(d)
        case _ => ()
      }
    }
    data += d
  }
}
