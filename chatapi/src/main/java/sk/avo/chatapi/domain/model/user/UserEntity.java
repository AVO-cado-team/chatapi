package sk.avo.chatapi.domain.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class UserEntity {
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
