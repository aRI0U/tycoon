package tycoon.objects.vehicle

import tycoon.ui.Renderable
import tycoon.objects.railway.Road
import tycoon.objects.structure._
import tycoon.game.{Player, GridLocation}
import scalafx.beans.property._
import tycoon.ui.Tile





sealed abstract class Direction
case object North extends Direction
case object East extends Direction
case object South extends Direction
case object West extends Direction
case object Undefined extends Direction


abstract class Vehicle(id: Int, struct: Structure, owner: Player) extends Renderable(new GridLocation(-1, -1)) {
  var weight : Double
  val cost : Int

  private var _speed = DoubleProperty(0)
  def speed: DoubleProperty = _speed

  def stabilize(pos: GridLocation, dt: Double, speed: Double): Boolean = {
    pos.percentageWidth = Math.max(pos.percentageWidth - dt * speed, 0)
    pos.percentageHeight = Math.max(pos.percentageHeight - dt * speed, 0)

    (pos.percentageHeight == 0 && pos.percentageWidth == 0)
  }

  // returns true iff the move lead to a change of case (ie percentage outbounds 0/100)
  def move(pos: GridLocation, dir: Direction, dt: Double, speed: Double): Boolean = {
    var changedSquare: Boolean = false
    dir match {
      case North =>
        pos.percentageHeight -= dt * speed
        if (pos.percentageHeight <= 0)
          { pos.setLoc(pos.top) ; pos.percentageHeight = 100 ; changedSquare = true }
      case East =>
        pos.percentageWidth += dt * speed
        if (pos.percentageWidth > 100)
          { pos.percentageWidth = 0 ; pos.setLoc(pos.right) ; changedSquare = true }
      case South =>
        pos.percentageHeight += dt * speed
        if (pos.percentageHeight > 100)
          { pos.percentageHeight = 0 ; pos.setLoc(pos.bottom) ; changedSquare = true }
      case West =>
        pos.percentageWidth -= dt * speed
        if (pos.percentageWidth <= 0)
          { pos.percentageWidth = 100 ; pos.setLoc(pos.left) ; changedSquare = true }
      case Undefined => ()
    }
    changedSquare
  }
}
