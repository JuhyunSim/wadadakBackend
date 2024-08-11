package com.example.runningservice.entity;

import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.JoinStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "crew_member", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "crew_id"})
})
@EntityListeners(AuditingEntityListener.class)
public class CrewMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private MemberEntity member;
    @ManyToOne
    @JoinColumn(name = "crew_id")
    private CrewEntity crew;
    @Enumerated(EnumType.STRING)
    private CrewRole role;
    @CreatedDate
    private LocalDateTime joinedAt;
    @Enumerated(EnumType.STRING)
    private JoinStatus status;
}