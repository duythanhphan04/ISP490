package com.devteria.identity_service.entity;

import com.devteria.identity_service.enums.GroupMemberStatus;
import com.devteria.identity_service.enums.GroupStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user_group")
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 255)
    @Column(name = "id", nullable = false)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    Group group;

    @Column(name = "created_at")
    Instant added_at;

    @Column(name = "is_delete", length = 255)
    @Enumerated(EnumType.STRING)
    GroupMemberStatus status;
}
