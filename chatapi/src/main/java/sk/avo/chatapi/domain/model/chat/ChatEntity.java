package sk.avo.chatapi.domain.model.chat;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import sk.avo.chatapi.domain.model.user.UserEntity;


@Entity
@Getter
@Setter
@Table(name = "chats")
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
