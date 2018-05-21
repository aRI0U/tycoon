package tycoon.game

import tycoon.game._
import tycoon.ui.Tile
import tycoon.objects.railway._
import tycoon.objects.structure._
import scala.xml.XML
import scala.collection.mutable.ListBuffer
import java.io.File
import scala.xml._
import java.io.PrintWriter
import scala.xml.transform.RuleTransformer
import scala.xml.transform.RewriteRule

class Saver(game : Game) {
  def createSaveFile(file : File, name : String) {
    val pw = new PrintWriter(file)
    try pw.write("<mxfile>\n</mxfile>") finally pw.close()
    val myXMLFile = XML.loadFile(file)
    val myXML = <mxfile></mxfile>

    val addMap = new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case elem: Elem if elem.label == "mxfile" =>
          elem.copy(child = (elem.child ++
          <Map name={name} width={game.map.width.toString} height={game.map.height.toString}>
          </Map>))
        case n => n
      }
    }
    // val city = game.towns(0)

    def addTown(town : Town) = {
      val add = new RewriteRule {
        override def transform(n: Node): Seq[Node] = n match {
          case elem: Elem if elem.label == "Map" =>
            elem.copy(child = (elem.child ++
            <City name={town.name} x={town.gridPos.row.toString} y={town.gridPos.col.toString} population={town.population.toString}>
            </City> ))
          case n => n
        }
      }
      add
    }
    def makeStringTile(x : Int ,y : Int, s : String) : String = {
      "<Tile type=\"" + s + "\" x=\"" + x.toString + "\" y=\"" + y.toString + "\">\n</Tile> \n"
    }
    def makeStringStruc(x : Int ,y : Int, s : String, pop : Int, name : String ) : String = {
      "<Structure type=\"" + s + "\" population=\"" + pop.toString + "\" name=\"" + name + "\" x=\"" + x.toString + "\" y=\"" + y.toString + "\">\n</Structure> \n"
    }
    def getXml () : String = {
      var x : String = "<Tiles datatype=\"buisness\">\n"
      for ((col : Int, row : Int) <- new GridRectangle(0, 0, game.map.width, game.map.height).iterateTuple) {
        game.map.getBackgroundTile(col,row) match {
          case Tile.Asphalt => x = x.concat(makeStringTile(col,row,"asphalt"))
          case Tile.Tree => x = x.concat(makeStringTile(col,row,"tree"))
          case Tile.Rock => x = x.concat(makeStringTile(col,row,"rock"))
          case t => {
            if (game.map.checkBgTile(col,row,Tile.Water)) x = x.concat(makeStringTile(col,row,"water"))
            else {if (game.map.checkBgTile(col,row,Tile.Sand)) x = x.concat(makeStringTile(col,row,"sand"))
              else x = x.concat(makeStringTile(col,row,"grass"))
            }
          }
        }
        game.map.maybeGetStructureAt(col,row) match {
          case Some(t : Town) => {x = x + makeStringStruc(col,row,"town",t.population,t.name)}
          case Some(f : Factory) => {x = x + makeStringStruc(col,row,"factory",f.workers, f.name)}
          case Some(f : Farm) => {x = x + makeStringStruc(col,row,"farm",f.workers, f.name)}
          case Some(m : Mine) => {x = x + makeStringStruc(col,row,"mine",m.workers, m.name)}
          case Some(a : Airport) => {x = x + makeStringStruc(col,row,"airport",0, a.name)}
          case Some(f : Field) => {x = x + makeStringStruc(col,row,"field",0,f.name)}
          case Some(p : PackingPlant) => {x = x + makeStringStruc(col,row,"paking",p.workers,p.name)}
          case Some(d : Dock) => {x = x + makeStringStruc(col,row,"dock",0,d.name)}
          case Some(w : WindMill) => {x = x + makeStringStruc(col,row,"windmill",0,w.name)}
          // case Some(r : Rail) => {x = x + makeStringStruc(col,row,"rail",0,"")}
          case _ => {}
        }
        println(col,row)
      }
      x + "</Tiles>"
    }
    def getXmlRails() : String = {
      var x : String = "<Roads datatype=\"nothing\">\n"
      for (vertex <- game.gameGraph.content) {
        for ((i,road) <- vertex.links ) {
          x = x + "<Road beginx=\"" + road.startStructure.get.gridPos.col.toString +"\" beginy=\"" + road.startStructure.get.gridPos.row.toString +"\" endx=\"" + road.endStructure.get.gridPos.col.toString +"\" endy=\"" + road.endStructure.get.gridPos.row.toString +"\">"
          for (rail <- road.rails ) {
            x = x + "<Rail x=\"" + rail.gridPos.col.toString + "\" y=\"" + rail.gridPos.row.toString + "\"> </Rail>"
          }
          x = x + "</Road>\n"
        }
      }
      println(x)
      x + "</Roads>"
    }
    def getXmlVehicle() : String = {
      var x : String = "<Vehicles datatype=\"nothing\">\n"
      for (train <- game.trains){
        x = x + "<Train locationx=\"" + train.location.gridPos.col.toString + "\" locationy=\"" + train.location.gridPos.row.toString + "\"> </Train>"
        // add type of train ect..
      }
      for (plane <- game.planes) {
        x = x + "<Plane locationx=\"" + plane.location.gridPos.col.toString + "\" locationy=\"" + plane.location.gridPos.row.toString + "\"> </Plane>"
      }
      for (boat <- game.boats) {
        x = x + "<Boat locationx=\"" + boat.location.gridPos.col.toString + "\" locationy=\"" + boat.location.gridPos.row.toString + "\"> </Boat>"
      }
      for (truck <- game.trucks) {
        x = x + "<Truck locationx=\"" + truck.location.gridPos.col.toString + "\" locationy=\"" + truck.location.gridPos.row.toString + "\"> </Truck>"
      }
      println(x)
      x + "</Vehicles>\n"
    }
    def addXml(xmlString : String) = {
      val add = new RewriteRule {
        override def transform(n: Node): Seq[Node] = n match {
          case elem: Elem if elem.label == "Map" =>
            elem.copy(child = (elem.child ++
              scala.xml.XML.loadString(xmlString)))
          case n => n
        }
      }
      add
    }

    var transform = new RuleTransformer(addMap)
    var xmld = transform(myXML)

    var a = getXml()
    transform = new RuleTransformer(addXml(a))
    xmld = transform(xmld)

    var b = getXmlRails()
    transform = new RuleTransformer(addXml(b))
    xmld = transform(xmld)

    var c = getXmlVehicle()
    transform = new RuleTransformer(addXml(c))
    xmld = transform(xmld)

    // Final treatment of xml
    val p = new scala.xml.PrettyPrinter(40, 2)
    val x : scala.xml.Node = scala.xml.XML.loadString(p.format(xmld))
    XML.save(name + ".xml", x)
    p.format(myXML)
  }
}
