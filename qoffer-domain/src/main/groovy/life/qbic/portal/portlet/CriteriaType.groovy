package life.qbic.portal.portlet

/**
 * This enum represents all criteria for which a customer can be searched
 *
 * A customer can be searched based on
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
enum CriteriaType {

    LAST_NAME("last name"),
    ADD_ADDRESS("additional address"),
    GROUP_NAME("group name"),
    CITY("city")

    final String value

    CriteriaType(String value){
        this.value = value
    }

}