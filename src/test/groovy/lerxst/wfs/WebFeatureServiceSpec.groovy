package lerxst.wfs

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class WebFeatureServiceSpec extends Specification implements ServiceUnitTest<WebFeatureService> {

     void "test something"() {
        expect:
        service.doSomething()
     }
}
