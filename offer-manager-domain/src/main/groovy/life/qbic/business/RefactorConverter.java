package life.qbic.business;

import life.qbic.business.offers.Offer;
import life.qbic.business.products.Product;
import life.qbic.business.products.dtos.ProductDraft;
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier;
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace;

/**
 * <b>ATTENTION: Only for refactor purposes. Meant to be removed.</b>
 * <ol>
 *   <li>use cases dürfen nur die neuen klassen verwenden (keine data-model-lib)</li>
 *   <li>apdapter (controller und presenter) benutzen diesen converter</li>
 *   <li>sobald alles umgestellt ist kann man den offer manager laufen lassen</li>
 * </ol>
 */
public class RefactorConverter {

  // CreateOfferContent can be replaced by the OfferCalculus
  // vat calculation logic in the offer calculus can be redirected to the Tax office /Tax office can
  // be removed
  // ProductDraft and ProductEntity can be replaced

  Offer toOffer(life.qbic.datamodel.dtos.business.OfferId offerId) {
    return null;
  }

  Offer toOffer(life.qbic.datamodel.dtos.business.Offer offerDto) {
    return null;
  }

  life.qbic.datamodel.dtos.business.Offer toOfferDto(Offer offer) {
    return null;
  }

  Product toProduct(ProductDraft productDraft) {
    return null;
  }

  life.qbic.datamodel.dtos.business.services.Product toProductDto(Product product) {
    return null;
  }


  String toProductId(life.qbic.datamodel.dtos.business.ProductId productId) {
    return null; // todo implement conversion to String representation
  }

  life.qbic.datamodel.dtos.business.ProductId toProductIdDto(String productId) {
    return null; // todo implement parser for DTO conversion
  }

  // Todo: create ProjectSpace business entity
  ProjectSpace toProjectSpace(ProjectSpace projectSpace) {
    return null;
  }

  // Todo: Create ProjectIdentifier business entity
  ProjectIdentifier toProjectIdentifier(ProjectIdentifier projectIdentifier) {
    return null; // Todo: implement
  }


}
