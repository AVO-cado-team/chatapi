package sk.avo.chatapi.domain.model.chat;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sk.avo.chatapi.domain.model.user.UserEntity;

import java.util.Set;


@Entity
@Getter
@Setter
public class ChatEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Size(min = 3, max = 50)
  private String name;

  @ManyToMany
  Set<UserEntity> users;

  @OneToMany
  Set<MessageEntity> messages;

  @PrePersist
  private void onCreate() {}

  @PreUpdate
  private void onUpdate() {}

  public ChatEntity() {}
}
