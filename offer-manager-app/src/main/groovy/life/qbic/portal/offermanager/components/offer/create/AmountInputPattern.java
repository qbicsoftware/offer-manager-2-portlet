package life.qbic.portal.offermanager.components.offer.create;

import java.util.regex.Pattern;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public enum AmountInputPattern {
  ATOMIC(Pattern.compile("^(-?)([0-9]+)$")),
  PARTIAL(Pattern.compile("^(-?)([0-9]*)(.[0-9]+)?$"));

  private final Pattern pattern;

  AmountInputPattern(Pattern pattern) {
    this.pattern = pattern;
  }

  public boolean test(String s) {
    return pattern.asPredicate().test(s);
  }
}
