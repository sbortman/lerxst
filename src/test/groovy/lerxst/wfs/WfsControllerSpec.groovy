package lerxst.wfs

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class WfsControllerSpec extends Specification implements ControllerUnitTest<WfsController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
