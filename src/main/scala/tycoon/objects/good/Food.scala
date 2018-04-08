// package tycoon.objects.good
//
// case class Food(name: String) extends Product(name) {
//   def initStorageTime() : Double = {
//     label match {
//       case "Cake" => 150
//       case "Egg" => 300
//       case "Corn" => 1000
//       case _ => 0
//     }
//   }
//   val storageTime = initStorageTime()
//
//   def initNutritiousness() : Int = {
//     label match {
//       case _ => 1
//     }
//   }
//   val nutritiousness = initNutritiousness()
// }
//
// case class Product(newInstance: Unit => Good)
//
// object Product {
//   def newCake() = new Food("Cake")
//
//   val Cake = new Product(newCake)
// }
