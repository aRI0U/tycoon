package tycoon.game

import tycoon.objects.graph._
import tycoon.objects.structure._
import tycoon.objects.railway._
import tycoon.ui.Entity

import scalafx.collections.ObservableBuffer._
import scalafx.collections.ObservableBuffer
import scala.collection.mutable.{HashMap, ListBuffer}
import scalafx.beans.property.StringProperty

class RailManager {


  // Function to call in order to know if the rail is able to be seted.
  // Handle the tiles as well
  def IsSetable (rail : Rail, map: Map, rails : ListBuffer[Rail], gameGraph : Graph) : Boolean = {
    var rail_met : Int = 0
    val pos = rail.position

    def turning(o : Int, d : Int, rail_to_update : BasicRail) : Unit = {
        if ((o == 3 && d == 0) ||(d == 1 && o == 2) ) {
          rail_to_update.tile.getView.rotate = 180
          rail_to_update.nb_rotation = 0
        }
        if ((o == 0 && d == 1) ||(d == 2 && o == 3) ) {
          rail_to_update.tile.getView.rotate = 90
          rail_to_update.nb_rotation = 3
        }
        if ((o == 1 && d == 0) ||(d == 3 && o == 2) ){
          rail_to_update.tile.getView.rotate = 270
          rail_to_update.nb_rotation = 0
        }
    }
    def track_mergence (track : ListBuffer[Rail]) : Unit = {
        for (r <- track) {
          var temp =  r.next
          r.next = r.previous
          r.previous = temp
        }
    }
    //According to the surrounding entities, will create roads, change tiles
    def LookAround(pair : Any) : Boolean =  {pair match {
      case (s: Structure, i : Int) => {
        s match {
          case town : Town => {
            println ("tycoon > game > RailCreationMananager.scala > LookAround: *LookAround in RailCreationManager* town case")
          }
          case other => {
            println ("tycoon > game > RailCreationMananager.scala > LookAround: *LookAround in RailCreationManager* other case")
          }
        }
          rail.orientation = i
          if (rail.road.startStructure == None) {
            rail.road.startStructure = Some(s)
            if (rail_met == 0) {
              rail.origin = i
            }
          }
          rail.road.endStructure = Some(s)
          if (!(rail.road.endStructure == rail.road.startStructure)) {
            rail.road.endStructure = Some(s)
            rail.road.finished = true
            rail.orientation = i
              for (rail_member <- rail.road.rails) {
              rail_member.road = rail.road
                rail_member.printData += Tuple2("Between", StringProperty(rail.road.startStructure.get.name))
              rail_member.printData += Tuple2("and", StringProperty(rail.road.endStructure.get.name))
            }
          }
          if (rail.road.finished) {
            false
          }
          else true

      }
      //transmission of road properties from the previous rail to the next one
      case (previous_rail: BasicRail,i : Int)=> {
        if ((previous_rail.road_head == true) && (previous_rail.road.finished == false)) {
          if (rail_met < 3) {
            rail_met+=1
          }
            rail.road.rails ++= previous_rail.road.rails
            rail.road.length += previous_rail.road.length
            println ("tycoon > game > RailCreationMananager.scala > LookAround: " + rail.road.rails)
              if (rail_met == 1) {
              rail.previous = previous_rail
              previous_rail.next = rail
            }
            else {
              // In case of rails track mergence, it's get unified (rail_met > 1)
              track_mergence(previous_rail.road.rails)
              rail.next = previous_rail
              previous_rail.previous = rail
              track_mergence(rail.road.rails)
            }
            if (rail.road.startStructure == None) {
              rail.road.startStructure = previous_rail.road.startStructure
            }
            else {
              // In this case, the road is finished
              if (!(rail.road.startStructure == previous_rail.road.startStructure)) {
                rail.road.endStructure = rail.road.startStructure
                rail.road.startStructure = previous_rail.road.startStructure
                rail.road.finished = true
                  gameGraph.newRoad(rail.road)
                for (rail_member <- rail.road.rails) {
                  rail_member.road = rail.road
                    rail_member.printData += Tuple2("Between", StringProperty(rail.road.startStructure.get.name))
                  rail_member.printData += Tuple2("and", StringProperty(rail.road.endStructure.get.name))
                }
              }
            }
            previous_rail.road_head = false
              rail.origin = i
            previous_rail.orientation = i
            // Choos a new tile for previous rail if turning
            //actualy the update rail is not the  one used most part of the time... the one below stays..
            if ((previous_rail.origin +  previous_rail.orientation) % 2 ==1) {
              // entities-= previous_rail    TODO change la rotation du tile au lieu de supprimer / rajouter
              rails-=previous_rail
              val previous_rail_update = previous_rail.copy(tile_type = 1)
              // previous_rail.next.previous = previous_rail_update
              // previous_rail.previous.next = previous_rail_update
              // val previous_rail_update = previous_rail.copy(previous_rail.pos, 1)
              previous_rail_update.previous = previous_rail.previous
              previous_rail_update.next = previous_rail.next
              previous_rail_update.road = previous_rail.road
              // entities+= previous_rail_update   TODO
              rails+= previous_rail_update
              turning(previous_rail.origin,previous_rail.orientation,previous_rail_update)
              previous_rail_update.road_head = false
            }
            // If needed, turn the straight rile tile
            if (rail.origin == 1 || rail.origin == 3) {
              rail.tile.getView.rotate = 90
            }
            true
          }
        else false
      }
      case _ => false
    }}
    //looking for a trail_head around there
    //list of surounding entities (Renderable)
    var env = new ListBuffer[Any]
    //left is probably actualy below ect...
    var boxleft = new GridLocation(pos.col, pos.row + 1)
    var boxright = new GridLocation(pos.col, pos.row-1)
    var boxup  = new GridLocation(pos.col+1, pos.row)
    var boxbelow  = new GridLocation(pos.col-1, pos.row)
    val boxes = Array(boxleft,boxup,boxright,boxbelow)
    /*for (other <- entities) {
      for (i : Int <- 0 to 3) {
        if (other.gridContains(boxes(i)) ) {
          val pair = (other,i)
          env += pair
        }
      }
    }*/
    for (i <- 0 to 3) {
      map.getContentAt(boxes(i)) match {
        case Some(e) => env += (e, i)
        case None => ()
      }
    }
    
    var valid_bis : Boolean = false
    for (pair <- env) {
      if (LookAround(pair))
        valid_bis = true
    }
    if ((rail.origin == 1 || rail.origin == 3) && rail.road.length == 1 ) {
      rail.tile.getView.rotate = 90
    }
    return valid_bis
  }
}
