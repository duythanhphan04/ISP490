package com.devteria.identity_service.entity;
import com.devteria.identity_service.enums.GroupDashboardStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "group_dashboard_access")
public class GroupDashboardAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 255)
    @Column(name = "id", nullable = false)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dashboard_id", nullable = false)
    Dashboard dashboard;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by", nullable = false)
    User grantedBy;

    @Column(name = "is_deleted", length = 255)
    @Enumerated(EnumType.STRING)
    GroupDashboardStatus status;
}
