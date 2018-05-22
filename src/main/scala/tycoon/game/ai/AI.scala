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

  money = 1000000

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

          case _ => action :: actions
        }
      }

      case Node(sons) => {
        chooseAction(sons(r.nextInt(sons.length)), actions)
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
              }
            }

            case _ => {
              val col = r.nextInt(game.map_width)
              val row = r.nextInt(game.map_height)
              val pos = new GridLocation(col, row)
              if (game.buyStruct(s, pos, this)) {
                game.setInfoText("AI created a new structure in " + col+row)
              }
            }
          }
        }

        case BuyVehicle(v) => {
          val structure = {
            v match {
              case _ => chooseRandomElement(towns)
            }
          }
          if (game.buyVehicle(v, structure.gridPos, this)) {
            game.setInfoText("AI added a new vehicle in " + structure.name)
          }
        }

        case _ => ()
      }
      waitingActions.tail
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
