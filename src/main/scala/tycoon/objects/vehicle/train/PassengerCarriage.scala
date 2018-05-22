package tycoon.objects.vehicle.train

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.objects.vehicle.PassengerTransporter
import tycoon.game.{GridLocation, Player, Settings}
import tycoon.ui.Tile

case class PassengerCarriage(_id: Int, initialTown: Structure, _owner: Player) extends Carriage(_id, initialTown, _owner) with PassengerTransporter {

  tile = Tile.PassengerWagonR
  val tiles = Array(Tile.PassengerWagonT, Tile.PassengerWagonR, Tile.PassengerWagonB, Tile.PassengerWagonL)

  remainingPlaces = Settings.TrainMaxPassengers
  val price = Settings.TrainTicketPrice
  val salesman = owner

  def embark(departureStruct: Structure, stops: ListBuffer[Structure]) = {
    owner.setCurrentVehicle(this)
    embarkP(departureStruct, stops)
  }

  def debark(s: Structure) = debarkP(s)
}
