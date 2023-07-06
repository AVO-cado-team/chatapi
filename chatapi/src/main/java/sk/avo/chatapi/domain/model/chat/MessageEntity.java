package sk.avo.chatapi.domain.model.chat;

import jakarta.persistence.*;
import java.util.Date;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sk.avo.chatapi.domain.model.user.UserEntity;


@Entity
@Getter
@Setter
@Table(name = "messages")
public class MessageEntity {
  @Id
  @NotNull
  private Long chatId;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long messageId;

  @ManyToOne @NotNull private ChatEntity chat;
  @ManyToOne @NotNull private UserEntity sender;
  @OneToOne private MessageEntity replyTo;

  @Column(length = 32, columnDefinition = "varchar(32) default 'TEXT'")
  @Enumerated(value = EnumType.STRING)
  @NotNull
  private MessageType type;

  @NotNull private String text;
  @NotNull private String content;
  @NotNull private Date timestamp;

  @PrePersist
  protected void onCreate() {
    timestamp = new Date();
  }
}
