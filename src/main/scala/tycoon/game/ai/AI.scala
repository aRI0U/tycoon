package tycoon.game.ai

import scala.util.Random

import tycoon.game._

class AI(game: Game) extends Player {

  val r = scala.util.Random

  name = "AI"

  val reactionTime : Double = Settings.AIReactionTime
  val decisionTime : Double = Settings.AIDecisionTime
  var internTime : Double = 0.0

  var active : Double = 0.0

  money = 1000000

  var decisionTree = Node.Root

  var waitingActions : List[Leaf] = List()

  def chooseAction(tree: DecisionTree = decisionTree, actions: List[Leaf] = waitingActions) : List[Leaf] = {
    val action = tree.searchAction()
    action match {

      case BuyStruct(s) => {
        s match {
          case BuyableStruct.Airport => {
            // if (towns.isEmpty) chooseAction(Node.BuyTownNode, action :: actions)
            // else action :: actions
            List()
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

      case _ => List() // should be "action :: actions" but there are bugs for the moment
    }
  }

  def executeAction() : List[Leaf] = {
    if (waitingActions.isEmpty) {
      waitingActions = chooseAction()
      internTime -= decisionTime
      waitingActions
    }
    else {
      println(waitingActions)
      waitingActions.head match {
        case BuyStruct(s) => {
          val col = r.nextInt(game.map_width)
          val row = r.nextInt(game.map_height)
          val pos = new GridLocation(col, row)
          if (game.buyStruct(s, pos, this)) {
            game.setInfoText("AI created a new structure in " + col+row)
          }
        }

        case BuyVehicle(v) => {
          val structure = {
            v match {
              case BuyableVehicle.Boat => docks(r.nextInt(docks.length))
              case BuyableVehicle.Plane => airports(r.nextInt(airports.length))
              case _ => towns(r.nextInt(towns.length))
            }
          }
          if (game.buyVehicle(v, structure.gridPos, this)) {
            game.setInfoText("AI added a new vehicle in " + structure.name)
          }
        }

        case _ => ()
      }
      active -= reactionTime
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
