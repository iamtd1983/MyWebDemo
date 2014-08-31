package controllers

import controllers.userController._
import helpers.configHelper
import reactivemongo.api.{MongoDriver, DefaultDB, MongoConnection}
import scala.util.{Try, Success, Failure}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
/**
 * Created by TD on 2014/8/24.
 */
object dbController {
  def getDB: DefaultDB = {
    val driver = new MongoDriver
    val uri = configHelper.getConfig("defaultDB", true)
    val conn: Try[MongoConnection] =
      MongoConnection.parseURI(uri).map { parsedUri =>
        driver.connection(parsedUri)
      }
    conn.get("MyTest")
  }
}
