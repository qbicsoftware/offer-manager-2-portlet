package life.qbic.portal.portlet.customers

import life.qbic.datamodel.dtos.business.Customer

import java.util.stream.Collectors

/**
  * A Filter to be applied to Customers
  *
  * This filter does provide functionality to filter a list of Customers for the given values.
  *
  * @since: 1.0.0
  */
class CustomerFilter {
    private final String firstName
    private final String lastName

    private CustomerFilter(Builder builder) {
        firstName = builder.firstName
        lastName = builder.lastName
    }

    /**
     * This method applies the current filter to a list and returns a filtered copy.
     * @param customers list that should be filtered
     * @return a filtered copy of the list
     * @since 1.0.0
     */
    List<Customer> apply(List<Customer> customers) {
        List copiedList = new ArrayList(Collections.unmodifiableList(customers))
        List filteredCustomers = copiedList.stream().find { Customer it ->
            return firstName? it.firstName == firstName : true
        }.find{ Customer it ->
            return lastName? it.lastName == lastName : true
        }.collect()
        return filteredCustomers
    }

    static class Builder {
        String firstName
        String lastName

        Builder() {
            firstName = null
            lastName = null
        }

        Builder firstName(String firstName) {
            this.firstName = firstName
            return this
        }

        Builder lastName(String lastName) {
            this.lastName = lastName
            return this
        }

        CustomerFilter build() {
            return new CustomerFilter(this)
        }
    }
}
