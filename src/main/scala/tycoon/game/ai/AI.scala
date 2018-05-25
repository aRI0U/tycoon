package tycoon.game.ai

import scala.collection.mutable.ListBuffer
import scala.util.Random

import tycoon.game._
import tycoon.objects.structure._
import tycoon.ui.Tile

class AI(game: Game) extends Player {

  val r = scala.util.Random

  val map = game.map
  val graph = game.gameGraph

  name = "AI"

  val reactionTime : Double = Settings.AIReactionTime
  val decisionTime : Double = Settings.AIDecisionTime
  var internTime : Double = 0.0

  var active : Double = 0.0

  money = 50000

  var decisionTree = Node.Root

  var waitingActions : List[Leaf] = List()

  def chooseRandomElement[A](l: ListBuffer[A]) : A = l(r.nextInt(l.length))

  def chooseAction(tree: DecisionTree = decisionTree, actions: List[Leaf] = waitingActions) : List[Leaf] = {

    tree match {
      case action: Leaf => {

        action match {

          case BuyStruct(s) => {
            s match {
              case BuyableStruct.Airport => {
                if (towns.filter(!_.hasAirport).isEmpty) chooseAction(Node.BuyTownNode, action :: actions)
                else action :: actions
              }
              case BuyableStruct.Dock => {
                // if (towns.isEmpty) chooseAction(Node.BuyTownNode, action :: actions)
                // else action :: actions
                List()
              }
              case BuyableStruct.Field => {
                if (farms.isEmpty) chooseAction(Node.BuyTownNode, action :: actions)
                else action :: actions
              }
              case _ => action :: actions
            }
          }

          case BuyVehicle(v) => {
            v match {
              case BuyableVehicle.Boat => {
                if (docks.isEmpty) chooseAction(BuyStruct.Dock, action :: actions)
                else action :: actions
              }
              case BuyableVehicle.Plane => {
                if (airports.isEmpty) chooseAction(BuyStruct.Airport, action :: actions)
                else action :: actions
              }
              case _ => {
                if (towns.isEmpty) chooseAction(Node.BuyTownNode, action :: actions)
                else action :: actions
              }
            }
          }

          case BuyRoad(item) => {
            item match {
              case BuyableStruct.Airport => {
                if (towns.isEmpty) chooseAction(Node.BuyTownNode,(chooseAction(Node.BuyTownNode, (chooseAction(BuyStruct.Airport, actions)))))
                else if (towns.length == 1) chooseAction(Node.BuyTownNode, (chooseAction(BuyStruct.Airport, actions)))
                else if (airports.length < towns.length) chooseAction(BuyStruct.Airport, actions)
                else actions
              }

              case r: BuyableRoad => {
                r match {
                  case BuyableRoad.Water => {
                    if (docks.length < 2) chooseAction(BuyStruct.Dock, actions)
                    else {
                      val d1 = chooseRandomElement(docks)
                      val d2 = chooseRandomElement(docks.filter(_ != d1))
                      val authorizedTiles = Tile.Grass ++ Tile.Water
                      val path = Dijkstra.tileGraph(d1, d2, authorizedTiles, map, 0)
                      val route = new RouteLeaf(path.toList, r)
                      route :: actions
                    }
                  }

                  case _ => {
                    if (actions.isEmpty || actions.head == OtherAction.TownToTown) {
                      if (towns.length < 2) chooseAction(Node.BuyTownNode, actions)
                      else {
                        val t1 = chooseRandomElement(towns)
                        val t2 = chooseRandomElement(towns.filter(_ != t1))
                        val path = Dijkstra.tileGraph(t1, t2, Tile.Grass, map, 0)
                        val route = new RouteLeaf(path.toList, r)
                        route :: actions
                      }
                    }
                    else if (actions.head == OtherAction.TownToFacility) {
                      if (towns.isEmpty) chooseAction(Node.BuyTownNode, actions)
                      else if (facilities.isEmpty) chooseAction(Node.BuyFacilityNode, actions)
                      else {
                        val t1 = chooseRandomElement(towns)
                        val f2 = chooseRandomElement(facilities)
                        val path = Dijkstra.tileGraph(t1, f2, Tile.Grass, map, 0)
                        val route = new RouteLeaf(path.toList, r)
                        route :: actions
                      }
                    }
                    else {
                      if (facilities.length < 2) chooseAction(Node.BuyFacilityNode, actions)
                      else {
                        val f1 = chooseRandomElement(facilities)
                        val f2 = chooseRandomElement(facilities.filter(_ != f1))
                        val path = Dijkstra.tileGraph(f1, f2, Tile.Grass, map, 0)
                        val route = new RouteLeaf(path.toList, r)
                        route :: actions
                      }
                    }
                  }
                }
              }
              case _ => actions
            }
          }

          case OtherAction.Trip => {

            if (towns.length < 2) { // not enough towns
              chooseAction(Node.BuyTownNode, actions)
            }
            else {
              println("towns: " + towns)
              val potential = new ListBuffer[BuyableVehicle]

              if (airports.length > 1 && airports.exists(!_.planeList.isEmpty)) {
                potential += BuyableVehicle.Plane
              }
               // train trip?
              val agents = towns ++ facilities
              val graphCopy = graph
              for (v <- graphCopy.content) {
              //  if (!agents.exists(_.structureId == v.origin)) graphCopy.removeStructureID(v.origin)
              }
              if (graphCopy.content.exists(!_.links.isEmpty)) {
                potential += BuyableVehicle.Train
              }
              if ((towns ++ facilities).exists(!_.truckList.isEmpty))
              potential += BuyableVehicle.Truck

              if (!potential.isEmpty) {
                val trip = new TripLeaf(chooseRandomElement(potential))
                trip :: actions
              }
              else chooseAction(Node.ConnectionNode, actions)
            }
          }

          case _ => action :: actions
        }
      }

      case Node(sons) => {
        tree match {
          case Node.TownToTownNode => {
            chooseAction(sons(r.nextInt(sons.length)), OtherAction.TownToTown :: actions)
          }
          case Node.TownToFacilityNode => {
            chooseAction(sons(r.nextInt(sons.length)), OtherAction.TownToFacility :: actions)
          }
          case Node.FacilityToFacilityNode => {
            chooseAction(sons(r.nextInt(sons.length)), OtherAction.FacilityToFacility :: actions)
          }
          case _ => chooseAction(sons(r.nextInt(sons.length)), actions)
        }
      }
    }
  }

  def executeAction() : List[Leaf] = {
    try {
      if (waitingActions.isEmpty) {
        internTime -= decisionTime
        chooseAction()
      }
      else {
        active -= reactionTime
        waitingActions.head match {
          case BuyStruct(s) => {
            s match {
              case BuyableStruct.Airport => {
                val potentialPos = new ListBuffer[GridLocation]
                for (t <- towns) {
                  if (!t.hasAirport) {
                    potentialPos ++= map.getSurroundingPos(t.gridPos, Tile.Grass)
                  }
                }
                if (game.buyStruct(s, chooseRandomElement(potentialPos), this)) {
                  game.setInfoText("AI created a new airport!")
                  waitingActions.tail
                }
                else List()
              }

              case _ => {
                val col = r.nextInt(game.map_width)
                val row = r.nextInt(game.map_height)
                val pos = new GridLocation(col, row)
                if (game.buyStruct(s, pos, this)) {
                  game.setInfoText("AI created a new structure in " + col+row)
                }
                waitingActions.tail
              }
            }
          }

          case BuyVehicle(v) => {
            val structure = {
              v match {
                case BuyableVehicle.Plane => chooseRandomElement(airports)
                case _ => chooseRandomElement(towns)
              }
            }
            if (game.buyVehicle(v, structure.gridPos, this)) {
              game.setInfoText("AI added a new vehicle in " + structure.name)
              waitingActions.tail
            }
            else List()
          }

          case RouteLeaf(posList, road) => {
            if (posList.isEmpty) waitingActions.tail
            else {
              if (game.buyRoad(road, posList.head, this)) {
                game.setInfoText("AI is creating a new road")
              }
              val stillToBuild = new RouteLeaf(posList.tail, road)
              stillToBuild :: waitingActions.tail
            }
          }

          case TripLeaf(v) => {
            v match {
              case BuyableVehicle.Plane => {
                if (airports.length > 1 && airports.exists(!_.planeList.isEmpty)) {
                  val t1 = chooseRandomElement(airports.filter(!_.planeList.isEmpty))
                  val t2 = chooseRandomElement(airports.filter(_ != t1))
                  game.createTrip(t1, t2, t1.planeList(0), r.nextInt(2) %2 == 0)
                  waitingActions.tail
                }
                else chooseAction(BuyRoad.Flight, waitingActions)
              }

              case BuyableVehicle.Train => {
                val agents = towns ++ facilities
                var destinations = new ListBuffer[Structure]
                for (a <- agents) {
                  for (v <- graph.content) {
                    if (a.structureId == v.origin && !v.links.isEmpty) destinations += a
                  }
                }
                destinations = destinations.filter(s => s.owner == this)
                if (destinations.length > 1 && !destinations.filter(!_.trainList.isEmpty).isEmpty) {
                  val a1 = chooseRandomElement(destinations.filter(!_.trainList.isEmpty))
                  val a2 = chooseRandomElement(destinations.filter(_ != a1))
                  try {
                    game.createRoute(graph.shortestRoute(a1, a2), ListBuffer(a1, a2), a1.trainList(0), r.nextInt(2) % 2 == 0)
                    waitingActions.tail
                  } catch {
                    case e: IllegalStateException => waitingActions.tail
                  }
                }
                else chooseAction(BuyRoad.Rail, waitingActions)
              }

              case BuyableVehicle.Truck => {
                val agents = towns ++ facilities
                val s1 = chooseRandomElement(agents.filter(!_.truckList.isEmpty))
                val s2 = chooseRandomElement(agents.filter(_ != s1))
                val path = Dijkstra.tileGraph(s1, s2, Array(Tile.Asphalt), map, 0)
                if (path.isEmpty) chooseAction(BuyRoad.Way)
                else {
                  game.createTrip(s1, s2, s1.truckList(0), r.nextInt(2) == 0 % 2)
                  waitingActions.tail
                }
              }

              case _ => waitingActions.tail
            }
          }

          case _ => waitingActions.tail
        }
      }
    } catch {
      case e: Exception => waitingActions
    }
  }



  def update(dt: Double) = {
    if (active > reactionTime) {
      internTime += dt
      if (internTime > decisionTime) {
        waitingActions = executeAction()
      }
    }
    else active += dt
  }
}
