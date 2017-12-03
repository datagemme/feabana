package generator

import java.util.UUID

import main.ExportOrder

object VizGen {

  def generateCharts(order: ExportOrder) = {
    val charts = order.viz.flatMap(viz => viz.chart match {
      case "bar" => Some(generateBarchart(order.index, viz.field, viz.fieldType, order.label))
      case "pie" => Some(generatePiechart(order.index, viz.field, viz.fieldType, order.label))
      case _ => None
    })

    charts.mkString("[",",","]")
  }

  private def generateBarchart(indexname: String, field: String, fieldType: String, label: String) = {

    val title = s"${label}_by_${field}_bar"

    val labelSubAgg = if(field != label) {
      s""",{\\\"id\\\":\\\"3\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"terms\\\",\\\"schema\\\":\\\"group\\\",\\\"params\\\":{\\\"field\\\":\\\"${label}.keyword\\\",\\\"size\\\":5,\\\"order\\\":\\\"desc\\\",\\\"orderBy\\\":\\\"1\\\"}}"""
    } else ""

    val kibanaFieldName = if(fieldType == "text") field+".keyword" else field
    val template =
      s"""
        |  {
        |    "_id": "${UUID.randomUUID.toString}",
        |    "_type": "visualization",
        |    "_source": {
        |      "title": "${title}",
        |      "visState": "{\\\"title\\\":\\\"${title}\\\",\\\"type\\\":\\\"histogram\\\",\\\"params\\\":{\\\"grid\\\":{\\\"categoryLines\\\":false,\\\"style\\\":{\\\"color\\\":\\\"#eee\\\"}},\\\"categoryAxes\\\":[{\\\"id\\\":\\\"CategoryAxis-1\\\",\\\"type\\\":\\\"category\\\",\\\"position\\\":\\\"bottom\\\",\\\"show\\\":true,\\\"style\\\":{},\\\"scale\\\":{\\\"type\\\":\\\"linear\\\"},\\\"labels\\\":{\\\"show\\\":true,\\\"truncate\\\":100},\\\"title\\\":{\\\"text\\\":\\\"${kibanaFieldName}: Descending\\\"}}],\\\"valueAxes\\\":[{\\\"id\\\":\\\"ValueAxis-1\\\",\\\"name\\\":\\\"LeftAxis-1\\\",\\\"type\\\":\\\"value\\\",\\\"position\\\":\\\"left\\\",\\\"show\\\":true,\\\"style\\\":{},\\\"scale\\\":{\\\"type\\\":\\\"linear\\\",\\\"mode\\\":\\\"normal\\\"},\\\"labels\\\":{\\\"show\\\":true,\\\"rotate\\\":0,\\\"filter\\\":false,\\\"truncate\\\":100},\\\"title\\\":{\\\"text\\\":\\\"Count\\\"}}],\\\"seriesParams\\\":[{\\\"show\\\":\\\"true\\\",\\\"type\\\":\\\"histogram\\\",\\\"mode\\\":\\\"stacked\\\",\\\"data\\\":{\\\"label\\\":\\\"Count\\\",\\\"id\\\":\\\"1\\\"},\\\"valueAxis\\\":\\\"ValueAxis-1\\\",\\\"drawLinesBetweenPoints\\\":true,\\\"showCircles\\\":true}],\\\"addTooltip\\\":true,\\\"addLegend\\\":true,\\\"legendPosition\\\":\\\"right\\\",\\\"times\\\":[],\\\"addTimeMarker\\\":false},\\\"aggs\\\":[{\\\"id\\\":\\\"1\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"count\\\",\\\"schema\\\":\\\"metric\\\",\\\"params\\\":{}},{\\\"id\\\":\\\"2\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"terms\\\",\\\"schema\\\":\\\"segment\\\",\\\"params\\\":{\\\"field\\\":\\\"${kibanaFieldName}\\\",\\\"size\\\":100,\\\"order\\\":\\\"desc\\\",\\\"orderBy\\\":\\\"1\\\"}}${labelSubAgg}],\\\"listeners\\\":{}}",
        |      "uiStateJSON": "{}",
        |      "description": "",
        |      "version": 1,
        |      "kibanaSavedObjectMeta": {
        |        "searchSourceJSON": "{\\\"index\\\":\\\"${indexname}\\\",\\\"query\\\":{\\\"match_all\\\":{}},\\\"filter\\\":[]}"
        |      }
        |    }
        |  }
      """.stripMargin

    template

  }




  private def generatePiechart(indexname: String, field: String, fieldType: String, label: String) = {
    val title = s"${label}_by_${field}_pie"
    val labelSubAgg = if(field != label) {
      s"""{\\\"id\\\":\\\"3\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"terms\\\",\\\"schema\\\":\\\"segment\\\",\\\"params\\\":{\\\"field\\\":\\\"${label}.keyword\\\",\\\"size\\\":5,\\\"order\\\":\\\"desc\\\",\\\"orderBy\\\":\\\"1\\\"}},"""
    } else ""

    val kibanaFieldName = if(fieldType == "text") field+".keyword" else field
    val template =
      s"""
         |  {
         |    "_id": "${UUID.randomUUID.toString}",
         |    "_type": "visualization",
         |    "_source": {
         |      "title": "${title}",
         |      "visState": "{\\\"title\\\":\\\"${title}\\\",\\\"type\\\":\\\"pie\\\",\\\"params\\\":{\\\"addTooltip\\\":true,\\\"addLegend\\\":true,\\\"legendPosition\\\":\\\"right\\\",\\\"isDonut\\\":false},\\\"aggs\\\":[{\\\"id\\\":\\\"1\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"count\\\",\\\"schema\\\":\\\"metric\\\",\\\"params\\\":{}},${labelSubAgg}{\\\"id\\\":\\\"2\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"terms\\\",\\\"schema\\\":\\\"segment\\\",\\\"params\\\":{\\\"field\\\":\\\"${kibanaFieldName}\\\",\\\"size\\\":5,\\\"order\\\":\\\"desc\\\",\\\"orderBy\\\":\\\"1\\\"}}],\\\"listeners\\\":{}}",
         |      "uiStateJSON": "{}",
         |      "description": "",
         |      "version": 1,
         |      "kibanaSavedObjectMeta": {
         |        "searchSourceJSON": "{\\\"index\\\":\\\"${indexname}\\\",\\\"query\\\":{\\\"match_all\\\":{}},\\\"filter\\\":[]}"
         |      }
         |    }
         |  }
      """.stripMargin

    template

  }
}
