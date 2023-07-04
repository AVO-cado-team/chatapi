package sk.avo.chatapi.domain.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sk.avo.chatapi.domain.model.chat.ChatModel;
import sk.avo.chatapi.domain.model.message.BaseMessageModel;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
public class UserModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  @Size(min = 3, max = 50)
  private String username;

  @Column(unique = true)
  @Size(min = 8, max = 255)
  @Email
  private String email;

  @ManyToMany Set<ChatModel> chats;
  @OneToMany Set<ChatModel> ownedChats;
  @OneToMany Set<BaseMessageModel> messages;

  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedAt;

  private String passwordHash;
  private Boolean isVerified;

  @PrePersist
  private void onCreate() {
    isVerified = false;
    updatedAt = new Date();
  }

  @PreUpdate
  private void onUpdate() {
    updatedAt = new Date();
  }
}
