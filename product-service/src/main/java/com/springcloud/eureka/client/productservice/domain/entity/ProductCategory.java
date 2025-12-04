package com.springcloud.eureka.client.productservice.domain.entity;

import com.javaauction.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_product_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    public static ProductCategory create(String name) {
        ProductCategory category = new ProductCategory();
        category.name = name;
        return category;
    }

    public void changeName(String name) {
        this.name = name;
    }
}
