package sk.avo.chatapi.domain.message.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sk.avo.chatapi.domain.chat.models.ChatModel;
import sk.avo.chatapi.domain.user.models.UserModel;

import java.util.Date;

@Entity
@Getter
@Setter
public class BaseMessageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @ManyToOne
    private ChatModel chat;

    @ManyToOne
    private UserModel sender;

    private String text;

    private String content;

    @Column(length = 32, columnDefinition = "varchar(32) default 'TEXT'")
    @Enumerated(value = EnumType.STRING)
    private MessageType type;

    private Date timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = new Date();
    }

}
