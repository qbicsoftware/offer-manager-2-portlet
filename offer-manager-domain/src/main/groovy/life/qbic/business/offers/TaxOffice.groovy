package life.qbic.business.offers

import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.services.ExternalServiceProduct
import life.qbic.datamodel.dtos.business.services.Product


/**
 * <b>Tax office that is responsible for offer tax calculation.</b>
 *
 * <p>Calculates the taxes that need to be considered for a given price value, affiliation and product type</p>
 *
 * <p>The current implementation takes a VAT ratio of 19%, default for Germany. Taxes only apply for customers within Germany
 * and if they are not part of the University or UKT. Taxes are not forwarded internally.</p>
 *
 * <p>The only exception are products (services) external services, for which taxes need to
 * be applied regardless of the affiliation status.</p>
 *
 * @since 1.2.0
 */
class TaxOffice {

    private static final BigDecimal VAT_RATIO = BigDecimal.valueOf(0.19)

    private final Affiliation affiliation

    /**
     * Creates a tax office that is configured by the affiliation.
     *
     * @param affiliation the affiliation the tax office instance needs to consider for calculations
     */
    TaxOffice(Affiliation affiliation) {
        this.affiliation = affiliation
    }

    /**
     * Calculates the absolute tax value that applies to a given service cost value and product type.
     *
     * @param serviceCosts the service cost value the office has to consider
     * @param product the service product
     * @return the absolute resulting tax value
     */
     BigDecimal applyTaxes(BigDecimal serviceCosts, Product product) {
        switch (affiliation) {
            case ({it.country != "Germany"}):
                return BigDecimal.valueOf(0)
                break
            case ({it.category.equals(AffiliationCategory.INTERNAL)}):
                return applyTaxesForInternals(serviceCosts, product.class)
                break
            default:
                return applyTaxesForExternals(serviceCosts)
                break
        }
    }

    private static BigDecimal applyTaxesForInternals(BigDecimal serviceCosts, Class clazz) {
        if (clazz.equals(ExternalServiceProduct)) {
            return serviceCosts * VAT_RATIO
        }
        return BigDecimal.valueOf(0)
    }

    private static BigDecimal applyTaxesForExternals(BigDecimal serviceCosts) {
        return serviceCosts * VAT_RATIO
    }
}
