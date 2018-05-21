package tycoon.game.ai

import scala.Array
import scala.util.Random

sealed abstract class DecisionTree {

  val r = scala.util.Random

  def randomSearch(t: DecisionTree = this) : Action = {
    t match {
      case Leaf(action) => action
      case Node(sons) => randomSearch(sons(r.nextInt(sons.length)))
    }
  }
}

case class Leaf(val action: Action) extends DecisionTree

case class Node(val sons : Array[DecisionTree]) extends DecisionTree

object Leaf {
  val BuySmallTownLeaf = new Leaf(BuyStruct.BuySmallTown)
  val BuyWindmillLeaf = new Leaf(BuyStruct.BuyWindmill)
}

object Node {
  val BuyTownNode = new Node(Array(Leaf.BuySmallTownLeaf))
  val BuyStructNode = new Node(Array(BuyTownNode, Leaf.BuyWindmillLeaf))
  val Root = new Node(Array(BuyStructNode))
}
