package dal

import controllers.dbController
import controllers.userController._
import models.User
import org.joda.time.DateTime
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
/**
 * Created by TD on 2014/8/31.
 */
object dalUser {
  // get connectionString
  val mydb = dbController.getDB
  //val db = conn("MyTest")
  //val collection = db[BSONCollection]("MyTest")
  val collection = mydb[BSONCollection]("User")

  def deleteUser(id: String) =  {
    // some business logic can implement here
    collection.remove(BSONDocument("_id" -> new BSONObjectID(id)))
  }

  def createUser(user: User) = {
    collection.insert(user.copy(creationDate = Some(new DateTime()), updateDate = Some(new DateTime())))
  }

  def editUser(id: String, updatedUser: BSONDocument) = {
    collection.update(BSONDocument("_id" -> new BSONObjectID(id)), updatedUser)
  }

  def getUser(id: String) = {
    collection.find(BSONDocument("_id" -> new BSONObjectID(id)))
  }

  def getUsers(query: BSONDocument) = {
    collection.find(query)
  }
}
