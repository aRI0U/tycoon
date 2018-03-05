package tycoon.ui

import scala.collection.mutable.ListBuffer
import scalafx.beans.property.StringProperty

trait Printable {

  val printData : ListBuffer[(String, StringProperty)] = ListBuffer()

}
