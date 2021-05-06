package com.chousun.tablelocktest.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "SHARED_LOCK_HOLDER")
public class SharedLockHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "RETRY_STATUS")
    String retryStatus;

    @Column(name = "LOCKED_BY")
    String lockedBy;

    @Column(name = "LOCKED_AT")
    LocalDateTime lockedAt;

    @Column(name = "LOCKED_TILL")
    LocalDateTime lockedTill;

    @Version
    @Column(name = "VERSION")
    Long version;

}
