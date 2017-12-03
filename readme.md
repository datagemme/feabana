# Feabana

Web application tool to generate Kibana vizualisations.

Developed for Elasticsearch & Kibana 5.X.

The aim is to make dashboard regeneration easy while Elasticsearch indexes can change over time.

Feabana was originally made for feature analysis and qualification on classification problematics.

# How to use it ?

Pull the latest docker image or build your own from sources.

Run the image, optionally overriding the following environment parameters :

| Command | Description | Default Value |
| --- | --- | --- |
| HTTP_INTERFACE | Http binding hostname for Feabana webapp | 0.0.0.0 |
| HTTP_PORT | Http binding port number for Feabana webapp | 5700 |
| ELASTIC_HOST | Elasticsearch master node hostname. Has to be overriden ! | localhost |
| ELASTIC_PORT | Elasticsearch port number | 9200 |

Example :
> docker run -d -p 5702:5702 -e HTTP_PORT=5702 --name feabana --network host datagemme/feabana

Open your browser at the deployed URL (default: http://localhost:5700)

Feabana will retrieve the existing indexes / aliases on Elasticsearch for you (from HTTP client)

You'll simply have to choose for which features / label you want to generate graphs and the graph type. (Only supporting BarChart & PieChart for now).

[[https://github.com/datagemme/feabana/blob/master/blob/img/Feabana.png]]

# Build from sources

To obtain a new image, simply run

> sbt pack docker