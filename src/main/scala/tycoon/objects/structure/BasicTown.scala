package structure

import scala.util.Random

abstract class BasicTown(x:Integer, y:Integer) extends Town(x, y) {
  var population = 50+r.nextInt(50)
}
