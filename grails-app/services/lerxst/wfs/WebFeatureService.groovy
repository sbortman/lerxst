package lerxst.wfs

import grails.web.servlet.mvc.GrailsParameterMap
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import groovy.xml.slurpersupport.GPathResult
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.uri.UriBuilder

import java.time.Duration

@CompileStatic
@Slf4j
class WebFeatureService {
  HttpClient wfsClient = HttpClient.create( 'http://localhost:8080'.toURL() )
  String path = '/geoserver/wfs'

  def doSomething( GrailsParameterMap params ) {
    def wfsParams = [
        service: params?.find { it?.key ==~ /(?i)service/ }?.value,
        version: params?.find { it?.key ==~ /(?i)version/ }?.value,
        request: params?.find { it?.key ==~ /(?i)request/ }?.value
    ]
    switch ( wfsParams[ 'request' ] ) {
    case ~/(?i)GetCapabilities/:
      log.info( 'GetCapabilities' )
      break
    case ~/(?i)DescribeFeatureType/:
      log.info( 'DescribeFeatureType' )
      switch ( wfsParams[ 'version' ] ) {
      case '1.0.0':
      case '1.1.0':
        wfsParams[ 'typeName' ] = params?.find { it?.key ==~ /(?i)typeName/ }?.value
        break
      case '2.0.0':
        wfsParams[ 'typeNames' ] = params?.find { it?.key ==~ /(?i)typeNames/ }?.value
        break
      }
      wfsParams[ 'outputFormat' ] = params?.find { it?.key ==~ /(?i)outputFormat/ }?.value
      break
    case ~/(?i)GetFeature/:
      log.info( 'GetFeature' )
      switch ( wfsParams[ 'version' ] ) {
      case '1.0.0':
      case '1.1.0':
        wfsParams[ 'maxFeatures' ] = params?.find { it?.key ==~ /(?i)maxFeatures/ }?.value
        wfsParams[ 'typeName' ] = params?.find { it?.key ==~ /(?i)typeName/ }?.value
        break
      case '2.0.0':
        wfsParams[ 'count' ] = params?.find { it?.key ==~ /(?i)count/ }?.value
        wfsParams[ 'typeNames' ] = params?.find { it?.key ==~ /(?i)typeNames/ }?.value
        break
      }
      wfsParams[ 'outputFormat' ] = params?.find { it?.key ==~ /(?i)outputFormat/ }?.value
      wfsParams[ 'filter' ] = params?.find { it?.key ==~ /(?i)filter/ }?.value
      wfsParams[ 'propertyName' ] = params?.find { it?.key ==~ /(?i)propertyName/ }?.value
      break
    }

    HttpResponse results = doGet( wfsParams )
    String body = results.body()

    body = body.replace( ':8080/geoserver', ':8081/lerxst' )

    [ contentType: results.contentType.get(), text: body ]
  }

  def doSomething( GPathResult body ) {
    String inputBody = groovy.xml.XmlUtil.serialize( body )
    HttpResponse results = doPost( inputBody )
    String outputBody = results?.body()

    log.info( body?.name() )
    log.info( inputBody )
    outputBody = outputBody.replace( ':8080/geoserver', ':8081/lerxst' )
    log.info( outputBody )

    [ contentType: results.contentType.get(), text: outputBody ]
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