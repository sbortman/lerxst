package lerxst.wfs

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.uri.UriBuilder

class WfsClient {
  HttpClient wfsClient
  URL url
  String path

  WfsClient( URL url, String path ) {
    wfsClient = HttpClient.create( url )
    this.url = url
    this.path = path
  }

  HttpResponse doGet( Map<String, Object> wfsParams ) {
    UriBuilder uri = UriBuilder.of( path )

    wfsParams.each { k, v -> uri.queryParam( k, v ) }
    wfsClient.toBlocking().exchange( HttpRequest.GET( uri.build() ), String )
  }

  HttpResponse doPost( String body ) {
    //wfsClient.configuration.readTimeout = Duration.ofSeconds(60)
    wfsClient.toBlocking().exchange( HttpRequest.POST( '/geoserver/wfs', body ), String )
  }

}
