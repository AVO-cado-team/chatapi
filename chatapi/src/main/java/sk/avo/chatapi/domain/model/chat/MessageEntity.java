package sk.avo.chatapi.domain.model.chat;

import jakarta.persistence.*;
import lombok.Data;
import sk.avo.chatapi.domain.model.user.UserEntity;

import java.util.Date;


@Entity
@Data
@Table(name = "messages")
@IdClass(MessageEntityId.class)
public class MessageEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
