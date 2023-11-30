package lerxst.wfs

import groovy.util.logging.Slf4j
import groovy.xml.XmlSlurper

@Slf4j
class WfsController {
  WebFeatureService webFeatureService

  def index() {
    def results

    switch ( request?.method ) {
    case 'GET':
      log.info( params as String )
      results = webFeatureService.doSomething( params )
      break
    case 'POST':
      def body = new XmlSlurper().parse( request?.inputStream )

      results = webFeatureService.doSomething( body )
      break
    default:
      results = [ contentType: 'text/plain', text: 'Invalid Request' ]
    }
    render results
  }
}