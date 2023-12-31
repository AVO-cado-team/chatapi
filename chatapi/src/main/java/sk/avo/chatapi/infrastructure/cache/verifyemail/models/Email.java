package sk.avo.chatapi.infrastructure.cache.verifyemail.models;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.avo.chatapi.infrastructure.cache.verifyemail.utils.RandomCode;

public class Email {
  private static final int CODE_LENGTH = 6;
  private final Logger LOG = LoggerFactory.getLogger(Email.class);

  @Getter private final String email;
  private String code;

  public Email(String email) {
    this.email = email;
  }

  public boolean isCodeValid(String code) {
    return this.code.equals(code);
  }

  public void generateCode() {
    LOG.debug("Generating code for email: " + this.email);
    code = RandomCode.generateCode(CODE_LENGTH);
    LOG.debug("Generated code: " + this.code);
  }

  /**
   * For testing purposes only
   *
   * @param code
   */
  public void setCode(String code) {
    this.code = code;
    LOG.info("Set code for email: " + this.email + " to: " + this.code);
  }
}
