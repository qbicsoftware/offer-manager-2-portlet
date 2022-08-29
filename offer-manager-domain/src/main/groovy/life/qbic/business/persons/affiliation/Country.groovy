package life.qbic.business.persons.affiliation
/**
 * Lists all countries based on the iso standard
 *
 * @since: 1.0.0
 *
 */
class Country {

    /**
     * Provides a lexicographically ordered ascending list of US English names
     * of all known countries. This list contains unique entries and no empty Strings.
     *
     * @return A list of unique country names in alphabetical order
     * @since 1.0.0
     */
    static List<String> availableCountryNames() {
        String[] twoLetterCountryCodes = Locale.getISOCountries()
        List<String> countryDisplayNames = new ArrayList<>(twoLetterCountryCodes.length)

        for (String countryCode : twoLetterCountryCodes) {
            countryDisplayNames << new Locale("", countryCode).getDisplayCountry()
        }

        countryDisplayNames.sort(Comparator.naturalOrder())

        return countryDisplayNames
    }
}
