package tycoon

import javafx.animation.AnimationTimer

import scala.collection.mutable.ListBuffer

import traits.Renderable
import structure._

class Game
{
  private class GameLoop extends AnimationTimer
  {
    var startNanoTime : Long = System.nanoTime()

    override def handle(currentNanoTime: Long)
    {
      var elapsedTime : Double = (currentNanoTime - startNanoTime) / 1000000000.0
      startNanoTime = currentNanoTime

      if (elapsedTime > 0.01)
        elapsedTime = 0.01

      println(1000000000.0 / elapsedTime + " FPS")

      update(elapsedTime)
    }
  }

  var townsList = new ListBuffer[Town]()

  def create_town (case_x : Integer,case_y : Integer) : Unit = {
    val T = new BasicTown(case_x, case_y)
    townsList += T
    entities += T
    // afficher la town
  }

  var trainsList = new ListBuffer[Train]()

  def create_train (case_x:Integer, case_y:Integer) : Unit = {
    val train = new BasicTrain(case_x, case_y)
    trainsList += train
    entities += train
    // afficher le train
  }

  private final val loop = new GameLoop()

  // private final val player = new Player

  def start () : Unit = {

    loop.start()

  }
  def pause () : Unit = {

  }
  def stop () : Unit = {

  }

  private def update(dt : Double) : Unit = {

    // update game

  }

}
