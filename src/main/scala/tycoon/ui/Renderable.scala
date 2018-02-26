package tycoon.ui

import scalafx.geometry.Rectangle2D
import scalafx.scene.image.ImageView

trait Renderable {
  protected val tile: Tile

  def getView : ImageView = tile.getView
  def viewport = tile.getView.viewport.get()

  def intersects (other: Renderable) : Boolean = {
    this.viewport.intersects(other.viewport)
  }

  def setScreenPos(x: Double, y: Double) = tile.setScreenPos(x, y)
}
