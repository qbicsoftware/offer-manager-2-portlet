package life.qbic.portal.offermanager.components.product

import life.qbic.datamodel.dtos.business.ProductId
import org.apache.commons.lang3.StringUtils

import java.util.function.BiPredicate

/**
 * <p>Tests an ProductId on whether a user defined String is included in the
 * textual representation or not.</p>
 *
 * @since 1.0.0-rc
 *
 */
class ProductIdContainsString implements BiPredicate<ProductId, String> {
    @Override
    boolean test(ProductId productId, String userInput) {
        if (userInput) {
            return StringUtils.containsIgnoreCase(productId.toString(), userInput)
        } else {
            return true
        }
    }
}