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
@IdClass(MessageId.class)
public class MessageEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long messageId;

  @Id
  private Long chatId;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  private UserEntity sender;
  @OneToOne private MessageEntity replyTo;

  @Enumerated(value = EnumType.STRING)
  private MessageType type;

  private String text;
  private String content;
  private Date timestamp;

  @PrePersist
  protected void onCreate() {
    timestamp = new Date();
  }
}
