package tycoon.objects.structure

import tycoon.ui.{Renderable, Printable}
import tycoon.GridLocation


abstract class Structure(pos: GridLocation, id: Int) extends Renderable {
  val structure_id = id
}
