package sk.avo.chatapi.domain.shared;


public interface BaseId<T extends BaseId<T>> {
  static <T extends BaseId<T>> T of(Long value, Class<T> clazz) {
    try {
      return clazz.getConstructor(Long.class).newInstance(value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
