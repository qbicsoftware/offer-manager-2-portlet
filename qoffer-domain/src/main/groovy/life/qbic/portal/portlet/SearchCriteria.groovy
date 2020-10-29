package life.qbic.portal.portlet

/**
 * A DTO describing a search criteria
 *
 * When searching for customers or offers different criteria can be defined in order to search for specific traits of an offer or customer
 *
 * @since: 1.0
 * @author: Jennifer Bödker
 *
 */
class SearchCriteria {

    final HashMap<CriteriaType,String> criteria

    SearchCriteria(HashMap<CriteriaType,String> criteria){
        this.criteria = criteria
    }
}
