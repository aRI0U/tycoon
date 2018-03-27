package tycoon.objects.graph

import scala.collection.mutable.ListBuffer

import tycoon.ui.Renderable
import tycoon.ui.Tile
import tycoon.objects.carriage._
import tycoon.objects.railway._
import tycoon.objects.structure._
import tycoon.objects.vehicle._
import tycoon.game.Game
import tycoon.game.GridLocation

class Route(itinerary: ListBuffer[Road], train: Train, game: Game) {
  private var onTheRoad = true



  var dir_indicator = 1

  var current_road: Option[Road] = None


  def manageTile(entity : Renderable, direction : Int) = {
    val tileList = new ListBuffer[Tile]()
    tileList+=(Tile.passengerWagonB,Tile.passengerWagonR,Tile.passengerWagonT,Tile.passengerWagonL,Tile.goodsWagonB,Tile.goodsWagonR,Tile.goodsWagonT,Tile.goodsWagonL,Tile.locomotiveB,Tile.locomotiveR,Tile.locomotiveT,Tile.locomotiveL)
    var entityType = 1
    entity match {
      case train : Train => entityType = 2
      case p : PassengerCarriage => entityType = 0
      case _ => ;
    }
    entity.tile = tileList(direction + 4*entityType)
  }


  def departure() = {
    train.boarding(itinerary)

    train.carriageList.foreach {
      carriage => carriage match {
        case p: PassengerCarriage => // juste nb de passager ? ou le faire dans boarding ?
          game.playerMoney.set(game.playerMoney.value + (p.max_passengers - p.remaining_places) * p.ticket_price)
        case _ => ()
      }
    }

    train.location match {
      case None => () // pb
      case Some(struct) => {

        carriageMouvment(struct.gridPos, None, train.carriageList)

        if (struct == itinerary(itinerary.size - 1).startStructure.get) dir_indicator = 0 // PAS DE GET
        else dir_indicator = 1

        train.location = None
        struct.list_trains -= train
        train.visible = true

        current_road = Some(itinerary.last)
        for (rail <- itinerary.last.rails) {
          if (rail.direction((dir_indicator - 1) %2) == rail) {
           train.current_rail = Some(rail)
          }
        }
        itinerary -= itinerary.last
        rotateVehicle(train)
        train.gridPos = (train.current_rail.get).gridPos
      }
    }

  }

  def arrival (road: Road) = {
    if (dir_indicator == 1) {train.location = road.startStructure}
    else {train.location = road.endStructure}
    train.location match {
      case Some(s) => s.list_trains += train
      case None => ()
    }
    train.landing()

    train.gridPos = train.location.get.gridPos.right
    if (itinerary.size == 0) {
      onTheRoad = false
      for (car <- train.carriageList) {
        car.gridPos = (new GridLocation(-1,-1))
        car.current_rail = None
      }
    }
    else carriageMouvment(train.current_rail.get.gridPos, train.current_rail, train.carriageList)
    train.current_rail = None
    train.visible = true
  }

  //train mouvment
  def update_box (road : Road) = {
    train.current_rail match {
      case Some(rail) => {
          if (rail.direction(dir_indicator) == rail) {
            arrival(road)
          }
          else {
            train.current_rail = Some(rail.direction(dir_indicator))
            rotateVehicle(train)
            train.gridPos = (rail.direction(dir_indicator).gridPos)
            carriageMouvment(rail.gridPos, Some(rail), train.carriageList)
        }
      }
      case None => {
      }
    }
  }


  def update (dt: Double) {
    intern_time += dt
    if (onTheRoad) {
      if (intern_time > 1) {
        intern_time -=1
        train.location match {
          case Some(town) => departure()
          case None => update_box(current_road.get)
        }
      }
    }
  }



  /// TWO NEXT FUNCTION COULD BE MERGE
// give the tile the orientation fitting with the rail
  def rotateVehicle (thing : Renderable) = {
    var r = new Rail(new GridLocation(-1,-1))
    thing match {
      case t : Train => r = t.current_rail.get
      case c : Carriage => r = c.current_rail.get
    }
    //choosing rail with witch we compare the direction tookeen by the train
    var comp_rail = r.direction((dir_indicator - 1) %2)
    var plus = 1
    if (!(r == r.direction(dir_indicator))) {
      comp_rail = r.direction(dir_indicator)
      plus = 0
    }
    var id = 3
    var x = r.gridPos.col - comp_rail.gridPos.col
    var y = r.gridPos.row - comp_rail.gridPos.row
    if (x == -1) {
      id = 1
    }
    if (y == 1) {
      id = 2
    }
    if (x == 1) {
      id = 3
    }
    if (y == -1) {
      id = 0
    }
    id = ((id + 2*plus) % 4)
    manageTile(thing, id)
  }

  def wagon_rotation (thing : Carriage) = {
    var r = thing.current_rail.get
    //choosing rail with witch we compare the direction tookeen by the train
    var comp_rail = r.direction((dir_indicator - 1) %2)
    var plus = 1
    if (!(r == r.direction(dir_indicator))) {
      comp_rail = r.direction(dir_indicator)
      plus = 0
    }
    var x = r.gridPos.col - comp_rail.gridPos.col
    var y = r.gridPos.row - comp_rail.gridPos.row
    if (x == 1) {
      thing.tile = Tile.passengerWagonT
    }
    if (x == -1) {
      thing.rotation(-90+ plus*180)
    }
    if (y == 1) {
      thing.rotation(180+ plus*180)
    }
    if (y == -1) {
      thing.rotation(0+ plus*180)
    }
  }
  protected var intern_time : Double = 0

def carriageMouvment(fisrstPosition : GridLocation,optionRail : Option[Rail], carriages : ListBuffer[Carriage]) {
  if (!carriages.isEmpty) {
    var pos1 = fisrstPosition
    var pos2 = fisrstPosition
    var optionRail1 = optionRail
    var optionRail2 = optionRail
    for (car <- carriages) {
      pos2 = car.gridPos
      optionRail2 = car.current_rail
      car.gridPos = (pos1)
      car.current_rail = optionRail1
      optionRail1 match {
        case Some(r) => rotateVehicle(car)
        case _ => ;
      }
      pos1 = pos2
      optionRail1 = optionRail2
    }
  }
}


}
