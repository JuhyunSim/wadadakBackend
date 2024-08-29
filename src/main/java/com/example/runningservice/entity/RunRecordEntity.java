package com.example.runningservice.entity;

import com.example.runningservice.util.converter.BooleanToIntegerConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "runRecord")
@EntityListeners(AuditingEntityListener.class)
public class RunRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @ManyToOne
    @JoinColumn(name = "run_goal_id", nullable = false)
    private RunGoalEntity goalId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity userId;

    private Integer distance;
    private Duration pace;
    private Duration runningTime;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Convert(converter = BooleanToIntegerConverter.class)
    private Integer isPublic;
}