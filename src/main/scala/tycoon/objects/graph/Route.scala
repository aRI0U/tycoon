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

class Route(itinerary : ListBuffer[Road], train : Train, game : Game) {

  var on_the_road = true
  var dir_indicator = 1


  var current_road : Option[Road]= None

  def TailManager(entitie : Renderable, direction : Int) = {
    val tileList = new ListBuffer[Tile]()
    tileList+=(Tile.passengerWagonB,Tile.passengerWagonR,Tile.passengerWagonT,Tile.passengerWagonL,Tile.goodsWagonB,Tile.goodsWagonR,Tile.goodsWagonT,Tile.goodsWagonL,Tile.locomotiveB,Tile.locomotiveR,Tile.locomotiveT,Tile.locomotiveL)
    var entitieType = 0
    entitie match {
      case train : Train => entitieType = 2
      case p : PassengerCarriage => entitieType = 1
    }
  }

  def departure () = {
    train.boarding()
    for (carriage <- train.carriages_list) {
      carriage match {
        case p:PassengerCarriage =>
         game.playerMoney.set(game.playerMoney.value + p.passengers * p.ticket_price)
        case _ => ()
      }
    }
    val start = train.location.get
    carriageMouvment(start.gridPos ,None, train.carriages_list)
    //to select the right direction according to the construction sens
    if (train.location.get == itinerary(itinerary.size - 1).startStructure.get) dir_indicator = 0
    else dir_indicator = 1

    train.location = None
    start.list_trains -= train
    train.visible = true
    current_road = Some(itinerary(itinerary.size - 1))
    itinerary.remove(itinerary.size - 1)
    for (rail <- (current_road.get).rails) {
      if (rail.direction((dir_indicator - 1) %2) == rail) {
       train.current_rail = Some(rail)
      }
    }
    train_rotation(train)
    train.gridPos = (train.current_rail.get).position
    //carriageMouvment(train.current_rail.get.gridPos, train.carriages_list)
    //train.current_rail = current_road.rails
  }

  def arrival (road: Road) = {
    if (dir_indicator == 1) {train.location = road.startStructure}
    else {train.location = road.endStructure}
    // println("tycoon > objects > graph > Route.scala > arrival: " + train.location.get.population)
    train.location match {
      case Some(s) => s.list_trains += train
      case None => ()
    }
    train.landing()

    train.gridPos = (new GridLocation(train.location.get.position.col +1,train.location.get.position.row))
    if (itinerary.size == 0) {
      on_the_road = false
      for (car <- train.carriages_list) {
        car.gridPos = (new GridLocation(-1,-1))
        car.current_rail = None
      }
    }
    else carriageMouvment(train.current_rail.get.gridPos, train.current_rail, train.carriages_list)
    train.current_rail = None
    train.visible = true
  }

  /// TWO NEXT FUNCTION COULD BE MERGE

// give the tile the orientation fitting with the rail
  def train_rotation (thing : Train) = {
    var r = thing.current_rail.get
    //choosing rail with witch we compare the direction tookeen by the train
    var comp_rail = r.direction((dir_indicator - 1) %2)
    var plus = 1
    if (!(r == r.direction(dir_indicator))) {
      comp_rail = r.direction(dir_indicator)
      plus = 0
    }
    var x = r.position.col - comp_rail.position.col
    var y = r.position.row - comp_rail.position.row
    if (x == 1) {
      thing.rotation(90 + plus*180)
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

  def wagon_rotation (thing : Carriage) = {
    var r = thing.current_rail.get
    //choosing rail with witch we compare the direction tookeen by the train
    var comp_rail = r.direction((dir_indicator - 1) %2)
    var plus = 1
    if (!(r == r.direction(dir_indicator))) {
      comp_rail = r.direction(dir_indicator)
      plus = 0
    }
    var x = r.position.col - comp_rail.position.col
    var y = r.position.row - comp_rail.position.row
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
        case Some(r) => wagon_rotation(car)
        case _ => ;
      }
      // (pos2 == pos1) match {
      //   case true => {
      //     car.gridPos = (pos1)
      //     car.current_rail = optionRail1
      //     wagon_rotation(car)
      //   }
      //   case false => {}
      // }
      pos1 = pos2
      optionRail1 = optionRail2
    }
  }
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
            train_rotation(train)
            train.gridPos = (rail.direction(dir_indicator).position)
            //carriages update mouvment
            carriageMouvment(rail.position, Some(rail), train.carriages_list)
            // if (!train.carriages_list.isEmpty) {
            //   var rail_chain1 : Option[Rail] = Some(rail)
            //   var rail_chain2 : Option[Rail] = Some(rail)
            //   for (car <- train.carriages_list) {
            //     rail_chain2 = car.current_rail
            //     car.current_rail = rail_chain1
            //     rail_chain1 match {
            //       case Some(r) => {
            //         car.gridPos = (r.position)
            //         wagon_rotation(car)
            //       }
            //       case None => {}
            //     }
            //     rail_chain1 = rail_chain2
            //   }
            // }
        }
      }
      case None => {
      }
    }
  }


  def update (dt : Double) {
    intern_time += dt
    if (on_the_road) {
      if (intern_time > 1) {
        intern_time -=1
        train.location match {
          case Some(town) => departure()
          case None => update_box(current_road.get)
        }
      }
    }
  }
}
