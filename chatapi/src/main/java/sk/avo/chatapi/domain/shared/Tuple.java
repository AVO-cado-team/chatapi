package sk.avo.chatapi.domain.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tuple <X, Y> {
  private X first;
  private Y second;
}
