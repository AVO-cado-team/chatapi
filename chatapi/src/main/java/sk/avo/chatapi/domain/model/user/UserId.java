package sk.avo.chatapi.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import sk.avo.chatapi.domain.shared.BaseId;

@Data @AllArgsConstructor
public class UserId {
  private final Long value;
}
