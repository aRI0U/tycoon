package tycoon.objects.vehicle

import tycoon.ui.Renderable
import tycoon.objects.railway.Road
import tycoon.objects.structure._
import tycoon.game.{Player, GridLocation}





sealed abstract class Direction
case object North extends Direction
case object East extends Direction
case object South extends Direction
case object West extends Direction


abstract class Vehicle(id: Int, struct: Structure, owner: Player) extends Renderable(new GridLocation(-1, -1)) {
  var weight : Double
  val cost : Int


  // returns true iff the move lead to a change of case (ie percentage outbounds 0/100)
  def moveTmp(pos: GridLocation, dir: Direction, dt: Double, speed: Double, moveNext: Boolean = true): Boolean = {
    var moved: Boolean = false
    dir match {
      case North =>
        pos.percentageHeight -= dt * speed
        if (pos.percentageHeight <= 0) {
          if (moveNext) { pos.percentageHeight = 100 ; pos.setLoc(pos.top) }
          else pos.percentageHeight = 0
          moved = true
        }
      case East =>
        pos.percentageWidth += dt * speed
        if (pos.percentageWidth > 100)
          { pos.percentageWidth = 0 ; pos.setLoc(pos.right) ; moved = true }
      case South =>
        pos.percentageHeight += dt * speed
        if (pos.percentageHeight > 100)
          { pos.percentageHeight = 0 ; pos.setLoc(pos.bottom) ; moved = true }
      case West =>
        pos.percentageWidth -= dt * speed
        if (pos.percentageWidth <= 0) {
          if (moveNext) { pos.percentageWidth = 100 ; pos.setLoc(pos.left) }
          else pos.percentageWidth = 0
          moved = true
        }
    }
    moved
  }
}
