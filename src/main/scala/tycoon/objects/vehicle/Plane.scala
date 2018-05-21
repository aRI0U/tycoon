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


class Plane(_id: Int, airport: Structure, val owner: Player) extends Vehicle(_id, airport, owner) with Container with PassengerTransporter {

  // dynamic values
  accDistance = 3.0
  decDistance = 5.0
  initialSpeed = 0.1

  def accFunction (d: Double) : Double = Math.sqrt(d)
  def decFunction (d: Double) : Double = Math.sqrt(d)

  // passenger transportation
  remainingPlaces = Settings.PlaneMaxPassengers
  val price = Settings.PlaneTicketPrice
  val salesman = owner

  val maxSpace : Double = 100
  var remainingSpace : Double = maxSpace
  val merchandises = new ListBuffer[Merchandise]
  val mManager = new MerchandisesManager

  var onTheRoad = BooleanProperty(false)
  tile = Tile.Plane
  var weight = 50
  var consumption = 2
  gridPos = location.gridPos.clone()

  consumption = 20

  override def departure() = {
    println("departure")
    super.departure()
  }

  override def boarding(stops: ListBuffer[Structure]) = {
    super.boarding(stops)
    embark(location, stops)
  }

  override def landing() = {
    super.landing
    debark(location)
  }

  def link(stops: ListBuffer[Structure]) : ListBuffer[Structure] = {
    val linked = new ListBuffer[Structure]
    for (stop <- stops) {
      stop match {
        case a: Airport => {
          a.dependanceTown match {
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
      case a: Airport => a.dependanceTown match {
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
      case a: Airport => a.dependanceTown match {
        case Some(town) => {
          super.debark(town) // goods
          debarkP(town) // passengers
        }
        case None => ()
      }
    }
  }
}
