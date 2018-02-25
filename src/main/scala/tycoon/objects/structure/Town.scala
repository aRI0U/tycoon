package tycoon.objects.structure


import scalafx.scene.image.Image

abstract class Town(x:Integer, y:Integer) extends Structure(x, y) {
  val r = scala.util.Random
  val name = "Jeanne"
  var population : Integer
  var waiting_passengers : Integer = 0
  var intern_time : Double = 0
  val sprite = "bite"
  def update_population (delta_t: Double) {
    intern_time = intern_time + delta_t
    if (intern_time > 1) {
      for (i <- 0 to population) {
        var i = r.nextInt(100)
        if (i == 0) {
          population = population+1
        }
      }
      intern_time = intern_time - 1
    }
  }


  var tileset = new Image("file:src/main/resources/tileset.png")
  var width = 32
  var height = 32
  var tileset_x = 32
  var tileset_y = 32
}
