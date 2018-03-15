package tycoon.objects.vehicle

import tycoon.ui.Entity
import tycoon.objects.railway.Road
import tycoon.objects.structure._


abstract class Vehicle(town:Town) extends Entity {
  val weight : Int
  val cost : Int
}
