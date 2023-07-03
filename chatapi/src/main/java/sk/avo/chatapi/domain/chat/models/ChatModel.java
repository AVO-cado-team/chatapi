package sk.avo.chatapi.domain.chat.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sk.avo.chatapi.domain.message.models.BaseMessageModel;
import sk.avo.chatapi.domain.user.models.UserModel;

import java.util.Date;
import java.util.Set;

@Entity
public class ChatModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Size(min = 3, max = 50)
    @Column(unique = true)
    @Getter
    @Setter
    private String name;

    @ManyToOne
    UserModel owner;

    @ManyToMany
    Set<UserModel> users;

    @OneToMany
    Set<BaseMessageModel> messages;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    private void onCreate() {
        createdAt = new Date();
        updatedAt = createdAt;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = new Date();
    }
    public ChatModel() {
    }

}
