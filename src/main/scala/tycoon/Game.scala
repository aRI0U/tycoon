package tycoon



import javafx.animation.AnimationTimer


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
