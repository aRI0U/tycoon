package tycoon.ui

import scala.collection.mutable.ListBuffer

import scalafx.beans.property.{BooleanProperty, DoubleProperty, IntegerProperty, StringProperty}

class PrintableElement(val name: String, val valueInt: IntegerProperty) {
  val valueStr = new StringProperty
  valueStr <== valueInt.asString
//  def newElement(d: PrintableElement)
}

class PrintableRankedElement(override val name: String, override val valueInt: IntegerProperty) extends PrintableElement(name, valueInt) {
  val visible = new BooleanProperty
  visible <== valueInt =!= IntegerProperty(0)
  val rangeInt = ListBuffer(IntegerProperty(0))
  val range = new IntegerProperty

  def initRange(data: ListBuffer[PrintableElement]) = {
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

  def newElement(d: PrintableRankedElement) = {
    val b = new BooleanProperty
    b <== (valueInt > d.valueInt)
    val aux = new IntegerProperty
    aux <== rangeInt.last
    rangeInt += new IntegerProperty
    rangeInt.last <== aux + boolToInt(b)
    range <== rangeInt.last
  }

  def boolToInt(b: BooleanProperty) : IntegerProperty = {
    if (b.value) IntegerProperty(1)
    else IntegerProperty(0)
  }
}

class PrintableTownProduct(override val name: String, override val valueInt: IntegerProperty, val price: DoubleProperty) extends PrintableRankedElement(name, valueInt) {
  valueStr <== valueInt.asString.concat(" for $").concat(price.asString)

  // def truncate(property: DoubleProperty) : DoubleProperty = {
  //   val truncated = new DoubleProperty
  //   truncated <== ((property * 100).asInt)/100
  // }
}

class PrintableData(s: String) {
  val label = s
  val data = new ListBuffer[PrintableElement]

  def newElement(name: String, property: IntegerProperty) = {
    data += new PrintableElement(name, property)
  }

  def newRankedElement(name: String, property: IntegerProperty) = {
    val d = new PrintableRankedElement(name, property)
    d.initRange(data)
    for (elt <- data) {
      elt match {
        case rankedElt: PrintableRankedElement => rankedElt.newElement(d)
        case _ => ()
      }
    }
    data += d
  }

  def newTownProduct(name: String, property: IntegerProperty, price: DoubleProperty) = {
    val d = new PrintableTownProduct(name, property, price)
    d.initRange(data)
    for (elt <- data) {
      elt match {
        case rankedElt: PrintableRankedElement => rankedElt.newElement(d)
        case _ => ()
      }
    }
    data += d
  }
}
