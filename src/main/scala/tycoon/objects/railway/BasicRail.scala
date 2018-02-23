package railway

abstract class BasicRail(x: Integer, y:Integer) extends Rail(x, y) {
  val cost = 10
  val max_speed = 50
  val max_weight = 1000
}
