package tycoon.game.ai

import scala.collection.mutable.ListBuffer
import scala.util.Random

import tycoon.game._
import tycoon.objects.structure._
import tycoon.ui.Tile

class AI(game: Game) extends Player {

  val r = scala.util.Random

  val map = game.map

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

    println(actions)

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

          case BuyRoad(r) => {
            r match {
              case BuyRoad.Flight => {
                if (towns.length < 2) chooseAction(Node.BuyTownNode, actions)
                else {
                  if (towns.filter(_.hasAirport).length < 2) chooseAction(BuyStruct.Airport, actions)
                  else actions
                }
              }

              case BuyRoad.Canal => {
                if (docks.length < 2) chooseAction(BuyStruct.Dock, actions)
                else {
                  val d1 = chooseRandomElement(docks)
                  val d2 = chooseRandomElement(docks.filter(_ != d1))
                  val authorizedTiles = Tile.Grass ++ Tile.Water
                  val path = Dijkstra.tileGraph(d1, d2, authorizedTiles, map, 0)
                  val route = new RouteLeaf(path.toList, Tile.Water(0))
                  route :: actions
                }
              }

              case _ => {
                val tile = {
                  r match {
                    case BuyRoad.Rail => Tile.StraightRailBT
                    case BuyRoad.Way => Tile.Asphalt
                    case _ => Tile.Grass(0)
                  }
                }
                if (actions.isEmpty || actions.head == OtherAction.TownToTown) {
                  if (towns.length < 2) chooseAction(Node.BuyTownNode, actions)
                  else {
                    val t1 = chooseRandomElement(towns)
                    val t2 = chooseRandomElement(towns.filter(_ != t1))
                    val path = Dijkstra.tileGraph(t1, t2, Tile.Grass, map, 0)
                    val route = new RouteLeaf(path.toList, tile)
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
                    val route = new RouteLeaf(path.toList, tile)
                    route :: actions
                  }
                }
                else {
                  if (facilities.length < 2) chooseAction(Node.BuyFacilityNode, actions)
                  else {
                    val f1 = chooseRandomElement(facilities)
                    val f2 = chooseRandomElement(facilities.filter(_ != f1))
                    val path = Dijkstra.tileGraph(f1, f2, Tile.Grass, map, 0)
                    val route = new RouteLeaf(path.toList, tile)
                    route :: actions
                  }
                }
              }
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
    if (waitingActions.isEmpty) {
      internTime -= decisionTime
      chooseAction()
    }
    else {
      println(waitingActions)
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
                println("airports: " + airports)
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
                waitingActions.tail
              }
              else List()
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

        case _ => waitingActions.tail
      }
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
