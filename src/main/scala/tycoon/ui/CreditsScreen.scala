package tycoon.ui


import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.beans.property._

import scalafx.application.Platform
import scalafx.geometry.{Pos, Insets}

import scalafx.scene.control.{Button, Label, ScrollPane}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout._
import scalafx.scene.text.{Text, Font}
import scalafx.scene.media.AudioClip
import scalafx.scene.transform.Rotate
import scalafx.util.Duration
import scalafx.animation.{Timeline, Interpolator}
import scalafx.stage.Screen
import scalafx.scene.image.Image

// MUSICS: http://freemusicarchive.org/genre/Chiptune/?sort=track_interest

class CreditsScreen extends StackPane
{
  private var onExit = new Runnable { def run() {} }

  def setOnExit(r : Runnable) = {
    onExit = r
  }

  stylesheets += "style/creditsscreen.css"
  id = "body"


  private val theme_trains = new AudioClip("file:src/main/resources/trains.mp3")
  private val theme_march = new AudioClip("file:src/main/resources/imperial_march.mp3")

  private val bg_trains = new Image("file:src/main/resources/trains.jpg")

  private val ctxt1 = new Label {
    text = "A long time ago in a galaxy far, far away...."
    wrapText = true
    prefWidth = 400
    styleClass += "credits"
  }
  private val ctxt2 = new Label {
    text = "ENS Cachan-Saclay"
    styleClass += "credits"
    styleClass += "bigger"
  }
  private val ctxt3 = new Label {
    text = "presents"
    styleClass += "credits"
  }
  private val ctxt4 = new Label {
    text = "The Tycoon Game"
    styleClass += "credits"
    styleClass += "bigger"
  }
  private val ctxt5 = new Label {
    text = "featuring"
    styleClass += "credits"
  }
  private val ctxt6 = new Label {
    text = """Alain Riou
  Nathan Lichtlé
    Thibaud Ardoin"""
    styleClass += "credits"
    styleClass += "bigger"
  }
  private val ctxt7 = new Label {
    text = """It is a dark time for the Cachanais. Although the Death Compilator has been destroyed, Imperial troops have driven the Rebel students from their hidden base and pursued them across the campus.

Evading the dreaded Imperial ENS leaders, a group of freedom fighters led by Nate Scalawalker has established a new secret game on the remote ice world of Github."""
    wrapText = true
    prefWidth = 400
    styleClass += "credits"
    styleClass += "smaller"
  }

  private val return_btn = new Button {
    text = "Return to main menu"
    id = "btn_exit"
    onMouseClicked = _ => {
      theme_march.stop()
      theme_trains.stop()
      onExit.run()
    }
  }

  ctxt1.transforms += new Rotate(-55, 300, 200, 20, Rotate.XAxis)
  ctxt2.transforms += new Rotate(-55, 300, 200, 20, Rotate.XAxis)
  ctxt3.transforms += new Rotate(-55, 300, 200, 20, Rotate.XAxis)
  ctxt4.transforms += new Rotate(-55, 300, 200, 20, Rotate.XAxis)
  ctxt5.transforms += new Rotate(-55, 300, 200, 20, Rotate.XAxis)
  ctxt6.transforms += new Rotate(-55, 300, 200, 20, Rotate.XAxis)
  ctxt7.transforms += new Rotate(-55, 300, 200, 20, Rotate.XAxis)

  children.add(ctxt1)
  children.add(ctxt2)
  children.add(ctxt3)
  children.add(ctxt4)
  children.add(ctxt5)
  children.add(ctxt6)
  children.add(ctxt7)

  children.add(return_btn)

  this.height.onChange {
    init()
  }


  def init() = {

    theme_trains.stop()
    theme_march.stop()
    theme_march.play()

    ctxt1.translateY = height.value
    ctxt2.translateY = height.value
    ctxt3.translateY = height.value
    ctxt4.translateY = height.value
    ctxt5.translateY = height.value
    ctxt6.translateY = height.value
    ctxt7.translateY = 2 * height.value

    return_btn.translateY = height.value

    val timeline = new Timeline {
      keyFrames = Seq(
        at (0.s) { ctxt1.translateY -> height.value },
        at (14.s) { ctxt1.translateY -> -height.value },
        at (6.s) { ctxt2.translateY -> height.value },
        at (20.s) { ctxt2.translateY -> -height.value },
        at (10.s) { ctxt3.translateY -> height.value },
        at (24.s) { ctxt3.translateY -> -height.value },
        at (14.s) { ctxt4.translateY -> height.value },
        at (28.s) { ctxt4.translateY -> -height.value },
        at (20.s) { ctxt5.translateY -> height.value },
        at (36.s) { ctxt5.translateY -> -height.value },
        at (25.s) { ctxt6.translateY -> height.value },
        at (42.s) { ctxt6.translateY -> -height.value },
        at (28.s) { ctxt7.translateY -> 2 * height.value },
        at (51.s) { ctxt7.translateY -> (-height.value / 2 - ctxt7.height.value - 100) }
      )
    }
    timeline.play()

    timeline.onFinished = _ => {
      theme_march.stop()
      theme_trains.play()

      background = new Background(Array(new BackgroundImage(bg_trains, BackgroundRepeat.NoRepeat, BackgroundRepeat.NoRepeat,
        BackgroundPosition.Center, new BackgroundSize(100, 100, true, true, true, false))))
      return_btn.translateY = -80
    }
  }

}
