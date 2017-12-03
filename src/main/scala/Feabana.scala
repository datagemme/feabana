package main

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.{Config, ConfigFactory}
import elasticsearch.ElasticClient
import generator.VizGen

import scala.concurrent.ExecutionContextExecutor
import scala.util.Properties

case class ExportOrder(index: String, viz: Seq[ExportViz], label: String)
case class ExportViz(field: String, fieldType: String, chart: String)

trait Service{
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  val logger: LoggingAdapter

  import io.circe.generic.auto._
  import io.circe.parser.decode
  import io.circe.syntax._

  val routes =
    pathSingleSlash {
      getFromResource("static/index.html")
    } ~ pathPrefix("static" / Segments){uri =>
      getFromResource(s"static/${uri.mkString("/")}")
    } ~ path("ping"){
      complete(StatusCodes.OK)
    } ~ path("indexes"){
      val indexes = ElasticClient.retrieveIndexes
      complete(StatusCodes.OK, indexes.mkString("[\"","\",\"","\"]"))
    } ~ path("indexdef" / Segment) {index =>
      val definition = ElasticClient.retrieveMappingForIndex(index)
      complete(StatusCodes.OK, definition.asJson.noSpaces)
    } ~ path("generate"){
      post {
        entity(as[String]) {data =>
          val exportOrder = decode[ExportOrder](data).right.toOption
          val content = exportOrder.map(VizGen.generateCharts).getOrElse(s"Error serializing ${data}")

          complete(content)

        }
      }
    }

}

object Feabana extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  private val config = ConfigFactory.load()
  def getConfig(param: String) = {
    Properties.envOrElse(param.toUpperCase.replace(".","_"), config.getString(param))
  }
  override val logger = Logging(system, getClass)

  val (host, port) = (getConfig("http.interface"), getConfig("http.port").toInt)
  Http().bindAndHandle(routes, host, port)

  println(s"Started Feabana on http://${host}:${port}/")

}
