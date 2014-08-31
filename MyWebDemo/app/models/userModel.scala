package models

import org.jboss.netty.buffer._
import org.joda.time.DateTime
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation.Constraints._

import reactivemongo.bson._

case class User(
  id: Option[BSONObjectID],
  firstName: String,
  lastName: String,
  number: String,
  creationDate: Option[DateTime],
  updateDate: Option[DateTime]
)

object User {
  implicit object UserBSONReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument): User =
      User(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("firstName").get,
        doc.getAs[String]("lastName").get,
        doc.getAs[String]("number").get,
        doc.getAs[BSONDateTime]("creationDate").map(dt => new DateTime(dt.value)),
        doc.getAs[BSONDateTime]("updateDate").map(dt => new DateTime(dt.value))
      )
  }
  implicit object UserBSONWriter extends BSONDocumentWriter[User] {
    def write(user: User): BSONDocument =
      BSONDocument(
        "_id" -> user.id.getOrElse(BSONObjectID.generate),
        "firstName" -> user.firstName,
        "lastName" -> user.lastName,
        "number" -> user.number,
        "creationDate" -> user.creationDate.map(date => BSONDateTime(date.getMillis)),
        "updateDate" -> user.updateDate.map(date => BSONDateTime(date.getMillis))
      )
  }
  val form = Form(
    mapping(
      "id" -> optional(of[String] verifying pattern(
        """[a-fA-F0-9]{24}""".r,
        "constraint.objectId",
        "error.objectId")),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "number" ->  nonEmptyText,
      "creationDate" -> optional(of[Long]),
      "updateDate" -> optional(of[Long])) { (id, firstName, lastName, number, creationDate, updateDate) =>
        User(
          id.map(new BSONObjectID(_)),
          firstName,
          lastName,
          number,
          creationDate.map(new DateTime(_)),
          updateDate.map(new DateTime(_)))
      } { user =>
        Some(
          (user.id.map(_.stringify),
            user.firstName,
            user.lastName,
            user.number,
            user.creationDate.map(_.getMillis),
            user.updateDate.map(_.getMillis)))
      })
}