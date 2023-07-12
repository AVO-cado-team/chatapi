package sk.avo.chatapi.domain.shared;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tuple <X, Y> {
  private X first;
  private Y second;
}
