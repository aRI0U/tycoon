package tycoon.game.ai

import scala.util.Random

import tycoon.game._

class AI(game: Game) extends Player {

  val r = scala.util.Random

  val reactionTime : Double = Settings.AIReactionTime
  val decisionTime : Double = Settings.AIDecisionTime
  var internTime : Double = 0.0

  var active : Boolean = false

  money = 1000000

  val decisionTree = Node.Root

  def update(dt: Double) = {
    if (!active) {
      internTime += dt
      if (internTime > decisionTime) {
        internTime -= decisionTime
        decisionTree.randomSearch() match {
          case StructureBuying(s) => {
            val col = r.nextInt(game.map_width)
            val row = r.nextInt(game.map_height)
            val pos = new GridLocation(col, row)
            if (game.buyStruct(BuyableStruct.SmallTown, pos, this)) {
              game.setInfoText("AI created a town in " + col+row)
            }
          }

          case _ => ()
        }
      }
    }
  }
}
