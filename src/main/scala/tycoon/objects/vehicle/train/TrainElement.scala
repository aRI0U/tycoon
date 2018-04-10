package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.game._
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle._
import scalafx.beans.property._
import tycoon.ui.Tile
import tycoon.game.{Game, GridLocation, Player}
import tycoon.ui.DraggableTiledPane


abstract class TrainElement(_id: Int, initialTown: Structure, _owner: Player) extends Vehicle(_id, initialTown, _owner) {


  val tiles: Array[Tile]

  var currentRail : Option[Rail] = None

  def rotate(dirIndicator: Int) = {
    currentRail match {
      case Some(rail) => {
        var changeDir = if (rail == rail.nextInDir(dirIndicator)) 1 else 0
        var nextRail = rail.nextInDir((dirIndicator + changeDir) % 2)

        var direction = 1
        if (nextRail.gridPos.eq(rail.gridPos.top)) direction = 0
        else if (nextRail.gridPos.eq(rail.gridPos.right)) direction = 1
        else if (nextRail.gridPos.eq(rail.gridPos.bottom)) direction = 2
        else if (nextRail.gridPos.eq(rail.gridPos.left)) direction = 3
        direction = (direction + 2 * changeDir) % 4

        tile = tiles(direction)
      }
      case None => ()
    }
  }

  // train movement
  def move(dt: Double, dirIndicator: Int): Boolean = {
    var result: Boolean = false
    currentRail match {
      case Some(rail) => {
        if (rail.nextInDir((dirIndicator + 1) % 2) == rail) // first rail
          rotate(dirIndicator)

        if (rail.nextInDir(dirIndicator) == rail) { // last rail (can also be first rail)
          if (stabilize(gridPos, dt, speed.value))
            arrived = true
        }
        else {
          var dir: Direction =
            if (rail.nextInDir(dirIndicator).gridPos.eq(gridPos.top)) North
            else if (rail.nextInDir(dirIndicator).gridPos.eq(gridPos.right)) East
            else if (rail.nextInDir(dirIndicator).gridPos.eq(gridPos.bottom)) South
            else if (rail.nextInDir(dirIndicator).gridPos.eq(gridPos.left)) West
            else Undefined

          if (!stabilized && stabilize(gridPos, dt, speed.value)) {
            stabilized = true
            rotate(dirIndicator)
            if (rail.nextInDir((dirIndicator + 1) % 2) != rail)
              result = true
          }
          if (stabilized) {
            if (super.move(gridPos, dir, dt, speed.value)) {
              stabilized = false
              currentRail = Some(rail.nextInDir(dirIndicator))
            }
          }
        }
      }
      case None => ()
    }
    result
  }
}
