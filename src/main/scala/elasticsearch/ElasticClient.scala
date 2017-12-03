package elasticsearch

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.http.HttpClient
import com.typesafe.config.ConfigFactory
import main.Feabana

case class Field(name: String, fieldType: String)
case class Mapping(indexType: String, fields: Seq[Field])

object ElasticClient {
  val config = ConfigFactory.load()
  val client = HttpClient(ElasticsearchClientUri(Feabana.getConfig("elastic.host"), Feabana.getConfig("elastic.port").toInt))

  import com.sksamuel.elastic4s.http.ElasticDsl._

  def retrieveIndexes = {
    client.execute{
      getAliases
    }.await.filterNot(_.name.startsWith(".")).map(_.name)
  }

  def retrieveMappingForIndex(index: String) = {
    client.execute{
      getMapping(index)
    }.await.flatMap(_.mappings).map{case (indexType: String, fields: Map[String, Map[String, String]]) =>
      Mapping(indexType, fields.map(t => Field(t._1, t._2.getOrElse("type",""))).toSeq.sortBy(_.name))
    }
  }


  def main(args: Array[String]): Unit = {
    println(ElasticClient.retrieveMappingForIndex("poss"))
  }
}
