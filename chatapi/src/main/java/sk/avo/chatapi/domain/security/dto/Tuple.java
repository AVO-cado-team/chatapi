package sk.avo.chatapi.domain.security.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tuple<X, Y> {
    private final X first;
    private final Y second;
}
