package com.javaauction.alertservice.domain.entity;

import com.javaauction.alertservice.domain.enums.AlertType;
import com.javaauction.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_alert")
public class Alert extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "alert_id", nullable = false)
    private UUID alertId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "content")
    private String content;

    private Alert(String userId, UUID productId, AlertType alertType, String content) {
        this.userId = userId;
        this.productId = productId;
        this.alertType = alertType;
        this.isRead = false;
        this.content = content;
    }

    public static Alert ofNewAlert(String userId, UUID productId, AlertType alertType, String content) {
        return new Alert(userId, productId, alertType, content);
    }

    public void alertRead() {
        this.isRead = true;
    }

}
