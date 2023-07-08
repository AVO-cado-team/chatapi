package sk.avo.chatapi.domain.model.chat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
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

  // FIXME security issue, if chat have millions of users, all of them will be loaded to memory
  @ManyToMany(fetch=FetchType.EAGER)
  private Set<UserEntity> users = new HashSet<>();

  public void addUsers(Set<UserEntity> users) {
    this.users.addAll(users);
  }

  public void addUser(@NotNull UserEntity user) {
      this.users.add(user);
  }

  public void removeUser(UserEntity user) {
      this.users.remove(user);
  }


//  @PrePersist
//  private void onCreate() {}
//
//  @PreUpdate
//  private void onUpdate() {}
//
//  public ChatEntity() {}
}
