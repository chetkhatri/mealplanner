package me.krobinson.mealplan

import com.twitter.finagle.{Service, Http}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.finch._

object Main extends TwitterServer {

  val getHealth: Endpoint[String] = get("health") { Ok("Hello, World!") } // TODO ascii art. obviously.
  val getMealPlan: Endpoint[String] = {
    get("mealplan" :: param("boardUrl") :: paramOption("days")) { (boardUrl: String, days: Option[String]) =>
      Ok(MealPlan(boardUrl, days).nospaces)
    }
  }.withHeader(("Access-Control-Allow-Origin", "*"))

  val api: Service[Request, Response] = (getHealth :+: getMealPlan).toService
  val server = Http.server.serve(":8080", api)

  onExit { server.close() }

  Await.ready(adminHttpServer)

}