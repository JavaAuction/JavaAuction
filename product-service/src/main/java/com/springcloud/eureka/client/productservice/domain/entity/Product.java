package com.springcloud.eureka.client.productservice.domain.entity;

import com.javaauction.global.infrastructure.entity.BaseEntity;
import com.springcloud.eureka.client.productservice.domain.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId; // username

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;   // S3 등 외부 저장소 URL

    @Column(name = "final_price")
    private Long finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false, length = 30)
    private ProductStatus status;

    public static Product create(String userId, String name, String description, String imageUrl) {
        Product product = new Product();
        product.userId = userId;
        product.name = name;
        product.description = description;
        product.imageUrl = imageUrl;
        product.status = ProductStatus.AUCTION_WAITING;
        return product;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void changeStatus(ProductStatus productStatus, Long finalPrice) {
        this.status =productStatus;
        this.finalPrice = finalPrice;
    }
}
