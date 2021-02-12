package life.qbic.business.customers

import org.apache.tools.ant.taskdefs.Local


/**
 * Lists all countries based on the iso standard
 *
 * @since: 1.0.0
 *
 */
class Country {

    static List<String> getISOCountries(){
        List<String> countryNames = []
        List<String> countryCodes = Locale.US.getISOCountries()
        countryCodes.each { it ->
                countryNames << new Locale("",it).getDisplayCountry() }
    }
}
