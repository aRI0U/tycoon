package tycoon.game.ai

import tycoon.game._

sealed abstract class Action

case object BuySth extends Action
case class StructureBuying(s: BuyableStruct) extends Action

object BuyStruct {
  val BuySmallTown = new StructureBuying(BuyableStruct.SmallTown)
  val BuyWindmill = new StructureBuying(BuyableStruct.WindMill)
}
