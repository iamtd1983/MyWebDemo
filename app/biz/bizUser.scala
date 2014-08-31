package biz

import controllers.userController._
import dal.dalUser
import models.User
import reactivemongo.bson.{BSONObjectID, BSONDocument}


/**
 * Created by TD on 2014/8/31.
 */
object bizUser {
  def deleteUser(id: String) =  {
    // some business logic can implement here
    dalUser.deleteUser(id)
  }

  def createUser(user: User) =  {
    // some business logic can implement here
    dalUser.createUser(user)
  }

  def editUser(id: String, updatedUser: BSONDocument) =  {
    // some business logic can implement here
    dalUser.editUser(id, updatedUser)
  }

  def getUser(id: String) = {
    dalUser.getUser(id)
  }

  def getUsers(query: BSONDocument) = {
    dalUser.getUsers(query)
  }
}
