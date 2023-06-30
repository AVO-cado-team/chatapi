package sk.avo.chatapi.domain.user.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Getter
    private Integer id;

    @Column(unique = true, name = "username")
    @Size(min = 3, max = 50)
    @NotNull
    @Getter
    @Setter
    private String username;

    @Column(unique = true, name = "email")
    @Size(min = 8, max = 255)
    @NotNull
    @Email
    @Getter
    @Setter
    private String email;

    @Column(name = "password_hash")
    @NotNull
    @Getter
    @Setter
    private String passwordHash;

    @Column(name = "is_verified")
    @NotNull
    @Getter
    @Setter
    private Boolean isVerified;

    @Column(name = "created_at")
    @NotNull
    @Getter
    @Setter
    private LocalDateTime createdAt;
}
