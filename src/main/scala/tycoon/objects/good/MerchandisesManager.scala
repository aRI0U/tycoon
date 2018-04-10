package tycoon.objects.good

import scala.collection.mutable.ListBuffer

import tycoon.objects.structure._

class MerchandisesManager {

  var stops = new ListBuffer[Structure]
  var notVisited = new ListBuffer[Boolean]
  var requests = new ListBuffer[(Structure, ListBuffer[Good])]
  // var flattenedRequests = new ListBuffer[Good]
  var currentStopIndex = 0

  def addStop(s: Structure) = {
    val i = stops.indexOf(s)
    if (i == -1) {
      println("just added a stop:"+s)
      stops += s
      notVisited += true
      requests += new Tuple2(s, determineRequests(s))
      // val req = determineRequests(s)
      // requests += req
      // req.foreach(p => if (flattenedRequests.indexOf(p) == -1) flattenedRequests += p)
      //distribution += new ListBuffer[Merchandise]
    }
    else {
      notVisited(i) = true
      requests(i) = new Tuple2(s, determineRequests(s))
    }
  }

  def determineRequests(structure: Structure) : ListBuffer[Good] = {
    var request = new ListBuffer[Good]
    structure match {
      case f: Factory => {
        for (l <- f.recipesList) {
          for (p <- l) request += p._1
        }
      }
      case t: Town => {
        for (i <- 0 to t.stock.requestsInt.length - 1) {
          if (t.stock.requests(i) > 0) request += t.stock.productsTypes(i)
        }
      }
      case _ => ()
    }
    println(structure+" needs "+request)
    request
  }

  def distribute(merchandises: ListBuffer[Merchandise], s: Structure) = {
    // basic
    var i = 0
    while (requests(i)._1 != s) i+=1
    println("distributing to "+requests(i))
    for (good <- requests(i)._2)
    s match {
      case a: EconomicAgent => a.stock.receiveMerchandises(good, merchandises, None)
      case _ => ()
    }
  }
}
