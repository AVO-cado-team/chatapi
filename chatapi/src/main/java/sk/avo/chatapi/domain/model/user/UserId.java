package sk.avo.chatapi.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class UserId {
  private final Long value;

  public UserId (String value){
    this.value = Long.valueOf(value);
  }
}
