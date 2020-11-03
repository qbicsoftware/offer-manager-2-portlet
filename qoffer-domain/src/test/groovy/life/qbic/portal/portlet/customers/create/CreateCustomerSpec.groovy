package life.qbic.portal.portlet.customers.create

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import spock.lang.Specification

/**
 * <short description>
 *
 * <detailed description>
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class CreateCustomerSpec extends Specification {
    def "create customer with string"() {
        Customer customer = new Customer("A", "B", "Prof. Dr.", "a.b@c.de", new ArrayList<Affiliation>())
        expect:
        customer != null
    }

    def "create customer with enum"() {
        Customer customer = new Customer("A", "B", AcademicTitle.PROFESSOR, "a.b@c.de", new ArrayList<Affiliation>())
        expect:
        customer != null
    }
}