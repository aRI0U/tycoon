package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.game.Game
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle._
import scalafx.beans.property._
import tycoon.ui.Tile
import tycoon.game.{Game, GridLocation, Player}
import tycoon.ui.DraggableTiledPane




class Train(_id: Int, initialTown: Structure, val owner: Player) extends TrainElement(_id, initialTown, owner) {

  // used in display
  // id

  // current structure if moving is false, origin structure otherwise


  val tiles = Array(Tile.LocomotiveT, Tile.LocomotiveR, Tile.LocomotiveB, Tile.LocomotiveL)


  def update(dt: Double, dirIndicator: Int) = {
    if (move(dt, dirIndicator))
      maybeDisplayNewCarriage()
    for (c <- carriageList)
      c.update(dt, dirIndicator)
  }


// a ne pas appeler si on va north ou west sur le premier rail (ni si on a un virage en premier rail)
// plus simple regarder qd currentRail du train change et alors afficher un nouveau carriage
// pas suffisant -> regarder si le train est 0/0% ?
  def maybeDisplayNewCarriage() = {
    if (carriageList.filter(_.visible == false).nonEmpty) {
      carriageList.filter(_.visible == false)(0).visible = true
    }
  }


  def rotateTrain(v: Vehicle, dirIndicator: Int) = {
    v match {
      case train: Train => {
        train.currentRail match {
          case Some(rail) => {
            var changeDir = if (rail == rail.nextInDir(dirIndicator)) 1 else 0
            var nextRail = rail.nextInDir((dirIndicator + changeDir) % 2)

            var direction = 1
            if (nextRail.gridPos.eq(rail.gridPos.top)) direction = 0
            else if (nextRail.gridPos.eq(rail.gridPos.right)) direction = 1
            else if (nextRail.gridPos.eq(rail.gridPos.bottom)) direction = 2
            else if (nextRail.gridPos.eq(rail.gridPos.left)) direction = 3
            direction = (direction + 2 * changeDir) % 4

            val tiles = Array(Tile.LocomotiveT, Tile.LocomotiveR, Tile.LocomotiveB, Tile.LocomotiveL)
            train.tile = tiles(direction)
          }
          case None => ()
        }
      }
      case _ => ()
    }
  }

  tile = Tile.LocomotiveT
  var weight = 50
  var carriageList = new ListBuffer[Carriage]()
  var from = StringProperty(initialTown.name)

  gridPos = location.gridPos.right
  carriageList foreach (_.visible = false)
  carriageList foreach (_.gridPos = location.gridPos.right)

  def addCarriage(carriage: Carriage): Unit = {
    carriageList += carriage
    carriage.speed <== speed
  }

  def departure(firstRail: Rail) = {
    currentRail = Some(firstRail)
    gridPos = firstRail.gridPos.clone()

    for (carr <- carriageList) {
      carr.currentRail = Some(firstRail)
      carr.gridPos = firstRail.gridPos.clone()
      carr.visible = false
    }

    super.departure()
  }

  override def arrival() = {
    for (carr <- carriageList) {
      carr.visible = false
      carr.currentRail = None
    }
    super.arrival()
  }

  override def boarding(stops: ListBuffer[Structure]) = {
    carriageList foreach (_.embark(location, stops))
    super.boarding(stops)
  }

  override def landing() = {
    carriageList.foreach(_.debark(location))
    super.landing()
  }
}
