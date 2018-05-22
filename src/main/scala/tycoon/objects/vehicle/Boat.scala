package tycoon.objects.vehicle

import scala.collection.mutable.ListBuffer

import tycoon.game.Game
import tycoon.objects.good._
import tycoon.objects.railway._
import tycoon.objects.structure._
import scalafx.beans.property._
import tycoon.ui.Tile
import tycoon.game.{Game, GridLocation, Player}
import tycoon.ui.DraggableTiledPane
import tycoon.game.Settings


class Boat(_id: Int, dock: Structure, override val owner: Player) extends Vehicle(_id, dock, owner) with Container with PassengerTransporter {
  val maxSpace : Double = 100
  var remainingSpace : Double = maxSpace
  val merchandises = new ListBuffer[Merchandise]
  val mManager = new MerchandisesManager

  // dynamic values
  accDistance = 8.0
  decDistance = 3.0
  initialSpeed = 0.1

  def accFunction (d: Double) : Double = Math.sqrt(d)
  def decFunction (d: Double) : Double = Math.sqrt(d)

  // passenger transportation
  remainingPlaces = Settings.BoatMaxPassengers
  val price = Settings.BoatTicketPrice
  val salesman = owner

  var onTheRoad = BooleanProperty(false)
  tile = Tile.Boat
  var weight = 50
  var consumption = 0.1
  gridPos = location.gridPos.clone()

  override def boarding(stops: ListBuffer[Structure]) = {
    super.boarding(stops)
    embark(location, stops)
  }

  override def landing() = {
    super.landing
    debark(location)
    debarkP(location)
  }

  def link(stops: ListBuffer[Structure]) : ListBuffer[Structure] = {
    val linked = new ListBuffer[Structure]
    for (stop <- stops) {
      stop match {
        case d: Dock => {
          d.dependanceTown match {
            case Some(town) => linked += town
            case None => ()
          }
        }
        case _ => linked += stop
      }
    }
    linked
  }

  override def embark(structure: Structure, stops: ListBuffer[Structure]) = {
    structure match {
      case d: Dock => d.dependanceTown match {
        case Some(town) => {
          println("stops = " + stops)
          val linkedStops = link(stops)
          println("linked = "+ linkedStops)
          super.embark(town, linkedStops) // goods
          embarkP(town, linkedStops) // passengers
          println(mManager.stops)
        }
        case None => ()
      }
      case _ => ()
    }
  }

  override def debark(structure: Structure) = {
    println(mManager.stops)
    structure match {
      case d: Dock => d.dependanceTown match {
        case Some(town) => {
          super.debark(town) // goods
          debarkP(town) // passengers
        }
        case None => ()
      }
    }
  }
}
