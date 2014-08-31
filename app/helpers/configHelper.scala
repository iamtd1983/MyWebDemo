package helpers

import play.Play

/**
 * Created by TD on 2014/8/26.
 */
object configHelper {
  val machineType = Play.application().configuration().getString("machineType")

  def getConfig(key: String, isPrefix: Boolean): String = {
    if (isPrefix) Play.application().configuration().getString(key + "_" + machineType)
    else Play.application().configuration().getString(key)
  }
}
