package controllers

import biz.bizUser
import org.joda.time.DateTime
import reactivemongo.api.collections.default.BSONCollection

import scala.concurrent.Future

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.{ MongoController, ReactiveMongoPlugin }

import reactivemongo.bson._

import models.User


object userController extends Controller /*with MongoController*/ {
  // list all user and sort them
  def getUsers = Action.async { implicit request =>
    // get a sort document (see getSort method for more information)
    val sort = getSort(request)
    // build a selection document with an empty query and a sort subdocument ('$orderby')
    val query = BSONDocument(
      "$orderby" -> sort,
      "$query" -> BSONDocument())
    val activeSort = request.queryString.get("sort").flatMap(_.headOption).getOrElse("none")
    // the cursor of documents
    val found = bizUser.getUsers(query).cursor[User]

    found.collect[List]().map { users =>
      Ok(views.html.users(users, activeSort))
    }.recover {
      case e =>
        e.printStackTrace()
        BadRequest(e.getMessage())
    }
  }

  def showCreationForm = Action {
    Ok(views.html.editUser(None, User.form))
  }

  def showEditForm(id: String) = Action.async {
    //val objectId = new BSONObjectID(id)
    // get the documents having this id (there will be 0 or 1 result)
    val futureUser = bizUser.getUser(id).one[User]

    futureUser.map { userOption =>
      // search for the matching attachments
      // find(...).toList returns a future list of documents (here, a future list of ReadFileEntry)
      Ok(views.html.editUser(Some(id), User.form.fill(userOption.get)))
    }
  }

  def create = Action.async { implicit request =>
    User.form.bindFromRequest.fold(
      errors => Future.successful(Ok(views.html.editUser(None, errors))),
      // if no error, then insert the article into the 'articles' collection
      user =>
        bizUser.createUser(user).map(_ =>
          Redirect(routes.userController.getUsers))
    )
  }

  def edit(id: String) = Action.async { implicit request =>
    User.form.bindFromRequest.fold(
      errors => Future.successful(Ok(views.html.editUser(Some(id), errors))),
      user => {
        // create a modifier document, ie a document that contains the update operations to run onto the documents matching the query
        val updatedUser = BSONDocument(
          // this modifier will set the fields 'updateDate', 'title', 'content', and 'publisher'
          "$set" -> BSONDocument(
            "updateDate" -> BSONDateTime(new DateTime().getMillis),
            "firstName" -> BSONString(user.firstName),
            "lastNAme" -> BSONString(user.lastName),
            "number" -> BSONString(user.number)))
        // ok, let's do the update

        bizUser.editUser(id, updatedUser).map { _ =>
          Redirect(routes.userController.getUsers)
        }
      })
  }

  def delete(id: String) = Action.async {
    bizUser.deleteUser(id).map(_ => Ok).recover { case _ => InternalServerError }
  }

  private def getSort(request: Request[_]) = {
    request.queryString.get("sort").map { fields =>
      val sortBy = for {
        order <- fields.map { field =>
          if (field.startsWith("-"))
            field.drop(1) -> -1
          else field -> 1
        }
        if order._1 == "firstName" || order._1 == "lastName" || order._1 == "number"
      } yield order._1 -> BSONInteger(order._2)
      BSONDocument(sortBy)
    }
  }
}
