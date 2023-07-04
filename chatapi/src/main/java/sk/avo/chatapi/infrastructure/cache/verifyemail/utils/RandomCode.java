package sk.avo.chatapi.infrastructure.cache.verifyemail.utils;

public class RandomCode {
  public static String generateCode(int length) {
    String code = "";
    for (int i = 0; i < length; i++) {
      int random = (int) (Math.random() * 36);
      if (random < 10) {
        code += random;
      } else {
        code += (char) (random + 55);
      }
    }
    return code.toUpperCase();
  }

  public static String generateCode() {
    return generateCode(6);
  }
}
