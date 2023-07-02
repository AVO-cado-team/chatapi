package sk.avo.chatapi.domain.security.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tuple<X, Y> {
    public final X first;
    public final Y second;
}
