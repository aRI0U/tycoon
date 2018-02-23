package tycoon

import traits.Renderable

import javafx.animation.AnimationTimer
import scalafx.collections.ObservableBuffer
import scalafx.collections.ObservableBuffer._


class Game
{
  var entities = new ObservableBuffer[Renderable]()

  private class GameLoop extends AnimationTimer
  {
    var startNanoTime : Long = System.nanoTime()

    override def handle(currentNanoTime: Long)    {
      var elapsedTime : Double = (currentNanoTime - startNanoTime) / 1000000000.0
      startNanoTime = currentNanoTime

      if (elapsedTime > 0.01)
        elapsedTime = 0.01

      println(1000000000.0 / elapsedTime + " FPS")

      update(elapsedTime)
    }
  }

  def create_town (case_x : Int,case_y : Int) : Unit = {

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
