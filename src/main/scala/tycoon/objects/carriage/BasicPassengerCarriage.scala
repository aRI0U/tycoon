package tycoon.objects.carriage

import tycoon.ui.Tile
import tycoon.ui.Sprite

class BasicPassengerCarriage extends PassengerCarriage {
  val cost = 20
  val weight = 100
  val max_passengers = 10
  var passengers = 0
  val tile = new Tile(Sprite.tile_locomotive)
}
