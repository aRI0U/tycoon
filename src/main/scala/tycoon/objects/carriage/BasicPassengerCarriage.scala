package tycoon.objects.carriage

import tycoon.game.GridLocation
import tycoon.ui.Tile
import tycoon.objects.railway._

class BasicPassengerCarriage extends PassengerCarriage {
  tile = Tile.passenger_wagon

  val cost = 20
  val ticket_price = 3
  val weight = 100
  val max_passengers = 10
  var current_rail : Option[Rail] = None
  var currentLoc = new GridLocation(-1,-1)
}
