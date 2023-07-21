package sk.avo.chatapi.infrastructure.cache.verifyemail.utils;

public class RandomCode {
  public static String generateCode(int length) {
    StringBuilder code = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int random = (int) (Math.random() * 36);
      if (random < 10) {
        code.append(random);
      } else {
        code.append((char) (random + 55));
      }
    }
    return code.toString().toUpperCase();
  }

  public static String generateCode() {
    return generateCode(6);
  }
}
